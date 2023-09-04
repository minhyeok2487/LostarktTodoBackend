package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 회원 찾기(캐릭터 리스트와 함께)
     */
    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(username + "은(는) 없는 회원입니다"));
    }

    /**
     * 회원 삭제(캐릭터 리스트와 함께)
     */
    public int deleteMember(String username) {
        return memberRepository.deleteByUsername(username);
    }

    /**
     * 회원가입
     */
    public Member createMember(MemberDto signupDto, List<Character> characterList) {
        // 중복체크
        if (memberRepository.existsByUsername(signupDto.getUsername())) {
            String message = signupDto.getUsername() + " 이미 존재하는 username 입니다.";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        if (characterList.isEmpty()) {
            String message = "등록된 캐릭터가 없습니다.";
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        Member member = Member.builder()
                .username(signupDto.getUsername())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .apiKey(signupDto.getApiKey())
                .characters(new ArrayList<>())
                .role(Role.USER)
                .build();
        characterList.stream().map(character -> member.addCharacter(character)).collect(Collectors.toList());

        return memberRepository.save(member);
    }

    /**
     * 로그인
     */
    public Member login(MemberLoginDto memberloginDto) {
        String username = memberloginDto.getUsername();
        Member member = findMember(username);
        if (passwordEncoder.matches(memberloginDto.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException("패스워드가 틀립니다.");
        }
    }

    /**
     * 캐릭터 Todo 업데이트
     */
    public List<CharacterCheckDto> updateTodo(String username, List<CharacterCheckDto> characterCheckDtoList) {
        List<Character> characterList = findMember(username).getCharacters();

        List<CharacterCheckDto> resultDtoList = new ArrayList<>();
        for (Character character : characterList) {
            for (CharacterCheckDto characterCheckDto : characterCheckDtoList) {
                if (character.getCharacterName().equals(characterCheckDto.getCharacterName())) {
                    character.getDayTodo().updateDayContent(characterCheckDto);

                    CharacterCheckDto result = CharacterCheckDto.builder()
                            .characterName(character.getCharacterName())
                            .chaosCheck(character.getDayTodo().getChaosCheck())
                            .guardianCheck(character.getDayTodo().getGuardianCheck())
                            .build();

                    resultDtoList.add(result);
                }
            }
        }
        return resultDtoList;
    }


    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * 캐릭터 리스트 업데이트
     */
    public List<Character> updateCharacterList(Member member, List<Character> characterList) {
        List<Character> beforeCharacterList = member.getCharacters();
        List<Character> charactersToUpdate = new ArrayList<>();
        List<Character> charactersToAdd = new ArrayList<>();
        List<Character> charactersToDelete = new ArrayList<>();

        for (Character updateCharacter : characterList) {
            updateCharacter.setMember(member);
            boolean found = false;
            for (Character beforeCharacter : beforeCharacterList) {
                if (beforeCharacter.getCharacterName().equals(updateCharacter.getCharacterName())) {
                    Character updated = beforeCharacter.updateCharacter(updateCharacter); // 캐릭터 정보 업데이트
                    charactersToUpdate.add(updated);
                    found = true;
                    break;
                }
            }
            if (!found) {
                charactersToAdd.add(updateCharacter);
            }
        }

        for (Character beforeCharacter : beforeCharacterList) {
            boolean found = false;
            for (Character updateCharacter : characterList) {
                if (beforeCharacter.getCharacterName().equals(updateCharacter.getCharacterName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                charactersToDelete.add(beforeCharacter);
            }
        }

        beforeCharacterList.removeAll(charactersToDelete);
        beforeCharacterList.addAll(charactersToAdd);

        return beforeCharacterList;
    }

    public Member updateSort(String username, List<CharacterSortDto> characterSortDtoList) {
        Member member = findMember(username);
        List<Character> beforeCharacterList = member.getCharacters();
        beforeCharacterList.stream().peek(
                character -> characterSortDtoList.stream()
                        .filter(characterSortDto -> character.getCharacterName().equals(characterSortDto.getCharacterName()))
                        .findFirst()
                        .ifPresent(characterSortDto -> character.setSortNumber(characterSortDto.getSortNumber())))
                .collect(Collectors.toList());

        return member;
    }
}
