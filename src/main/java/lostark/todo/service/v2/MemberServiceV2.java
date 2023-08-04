package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateListDto;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
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
public class MemberServiceV2 {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 회원 찾기(캐릭터 리스트와 함께0
     */
    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(username + "은(는) 없는 회원입니다"));
    }

    /**
     * 회원가입
     */
    public Member createMember(MemberSignupDto signupDto, List<Character> characterList) {
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
     * 캐릭터 리스트 업데이트
     */
    public CharacterUpdateListDto updateCharacterList(String username, CharacterUpdateListDto characterUpdateListDto) {
        List<Character> characterList = findMember(username).getCharacters();

        CharacterUpdateListDto resultDtoList = new CharacterUpdateListDto();
        for (Character character : characterList) {
            for (CharacterUpdateDto characterUpdateDto : characterUpdateListDto.getCharacterUpdateDtoList()) {
                if (character.getCharacterName().equals(characterUpdateDto.getCharacterName())) {
                    character.getCharacterDayContent().updateDayContent(characterUpdateDto);

                    CharacterUpdateDto result = CharacterUpdateDto.builder()
                            .characterName(character.getCharacterName())
                            .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                            .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                            .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                            .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                            .build();

                    resultDtoList.addCharacter(result);
                }
            }
        }
        return resultDtoList;
    }


}
