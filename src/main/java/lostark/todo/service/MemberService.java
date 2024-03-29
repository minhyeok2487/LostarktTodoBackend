package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Member login(MemberLoginDto memberloginDto) {
        String username = memberloginDto.getUsername();
        Member member = findMember(username);
        if (passwordEncoder.matches(memberloginDto.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException("이메일 또는 패스워드가 일치하지 않습니다.");
        }
    }

    /**
     * ApiKey가 있는 회원 리스트
     */
    public List<Member> findAllByApiKeyNotNull() {
        return memberRepository.findAllByApiKeyNotNull();
    }

    /**
     * 회원 찾기(캐릭터 리스트와 함께)
     */
    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다."));
    }

    public Member findMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 회원입니다."));
    }

    // 1차 회원가입
    public Member createMember(String mail, String password) {
        if (memberRepository.existsByUsername(mail)) {
            throw new IllegalArgumentException(mail + " 이미 존재하는 이메일 입니다.");
        }

        Member member = Member.builder()
                .username(mail)
                .password(passwordEncoder.encode(password))
                .characters(new ArrayList<>())
                .authProvider("none")
                .role(Role.USER)
                .build();

        return memberRepository.save(member);
    }

    // 유저 전환(구글 로그인 -> 일반 로그인)
    public Member changeAuthProvider(String mail, String password) {
        Member member = findMember(mail);
        if (member.getAuthProvider().equals("none")) {
            throw new IllegalArgumentException("소셜 로그인으로 가입된 회원이 아닙니다.");
        }
        member.changeAuthToNone(passwordEncoder.encode(password));

        return member;
    }

    /**
     * 회원가입 캐릭터 추가
     */
    public Member createCharacter(String username, String apiKey, List<Character> characterList) {
        Member member = findMember(username);
        characterList.stream().map(character -> member.addCharacter(character)).collect(Collectors.toList());
        member.setApiKey(apiKey);
        return member;
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
     * 캐릭터 업데이트 로직
     * 업데이트, 추가, 삭제
     */
    public List<Character> updateCharacterList(Member member, List<Character> updateCharacterList) {
        List<Character> beforeCharacterList = member.getCharacters();
        // 캐릭터 정보 업데이트와 새로운 캐릭터 추가
        for (Character updateCharacter : updateCharacterList) {
            updateCharacter.setMember(member);
            boolean found = false;

            //기존에 있는 캐릭터는 업데이트
            for (Character beforeCharacter : beforeCharacterList) {
                if (beforeCharacter.getCharacterName().equals(updateCharacter.getCharacterName())) {
                    beforeCharacter.updateCharacter(updateCharacter); // 캐릭터 정보 업데이트
                    found = true;
                    break;
                }
            }

            //기존에 존재하지 않는 캐릭터면 추가
            if (!found) {
                beforeCharacterList.add(updateCharacter);
            }
        }

        // 삭제된 캐릭터 삭제
        Iterator<Character> beforeCharacterIterator = beforeCharacterList.iterator();
        while (beforeCharacterIterator.hasNext()) {
            Character beforeCharacter = beforeCharacterIterator.next();
            boolean found = false;
            for (Character updateCharacter : updateCharacterList) {
                if (beforeCharacter.getCharacterName().equals(updateCharacter.getCharacterName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                beforeCharacterIterator.remove();
            }
        }
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

    /**
     * 회원 API KEY 업데이트
     */
    public void updateApiKey(Member member, String apiKey) {
        member.setApiKey(apiKey);
    }

    /**
     * 회원 중복된 캐릭터 삭제
     */
    public void removeDuplicateCharacters(Member member) {
        boolean duplicate = false;
        List<Character> characters = member.getCharacters();
        Set<String> characterNames = new HashSet<>();
        List<Character> charactersToRemove = new ArrayList<>();

        for (Character character : characters) {
            String characterName = character.getCharacterName();

            if (characterNames.contains(characterName)) {
                duplicate = true;
                charactersToRemove.add(character);
            } else {
                characterNames.add(characterName);
            }
        }

        for (Character characterToRemove : charactersToRemove) {
            characters.remove(characterToRemove);
        }

        if(duplicate) {
            log.info("중복 존재 - id: {}, username: {}", member.getId(), member.getUsername());
            log.info("삭제된 캐릭터 : {}", charactersToRemove);
        } else {
            throw new IllegalArgumentException("중복된 캐릭터가 없습니다.");
        }
    }

    public String findMemberFriend(Member toMember, Member fromMember) {
        if(toMember.getFriends().isEmpty()) {
            return "친구 요청";
        } else {
            for (Friends friend : toMember.getFriends()) {
                if(friend.getFromMember() == fromMember.getId()) {
                    for (Friends fromMemberFriend : fromMember.getFriends()) {
                        if(fromMemberFriend.getFromMember() == friend.getId()) {
                            if(friend.isAreWeFriend() && fromMemberFriend.isAreWeFriend()) {
                                return "친구";
                            }
                            else if(friend.isAreWeFriend() && !fromMemberFriend.isAreWeFriend()) {
                                return "친구 요청 진행중";
                            }
                            else if(!friend.isAreWeFriend() && fromMemberFriend.isAreWeFriend()) {
                                return "친구 요청 받음";
                            }
                            else {
                                return "친구 요청";
                            }
                        }
                    }
                }
            }
        }
        return "???";
    }

    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }
}
