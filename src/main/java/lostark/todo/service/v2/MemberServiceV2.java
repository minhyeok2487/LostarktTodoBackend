package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v2.dto.characterDto.CharacterUpdateDtoV2;
import lostark.todo.controller.v2.dto.characterDto.CharacterUpdateListDtoV2;
import lostark.todo.controller.v2.dto.memberDto.MemberSignupDtoV2;
import lostark.todo.controller.v2.dto.memberDto.MemberloginDtoV2;
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
     * 회원가입
     */
    public Member createMember(MemberSignupDtoV2 signupDto, List<Character> characterList) {
        if (memberRepository.existsByUsername(signupDto.getUsername())) {
            String errorMessage = signupDto.getUsername() + " 이미 존재하는 username 입니다.";
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
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

    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(username + "은(는) 없는 회원입니다"));
    }

    public Member login(MemberloginDtoV2 memberloginDtoV2) {
        String username = memberloginDtoV2.getUsername();
        Member member = findMember(username);
        if (passwordEncoder.matches(memberloginDtoV2.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException("패스워드가 틀립니다.");
        }
    }


    public CharacterUpdateListDtoV2 updateCharacterList(String username, CharacterUpdateListDtoV2 characterUpdateListDtoV2) {
        List<Character> characterList = findMember(username).getCharacters();

        CharacterUpdateListDtoV2 resultDtoList = new CharacterUpdateListDtoV2();
        for (Character character : characterList) {
            for (CharacterUpdateDtoV2 characterUpdateDtoV2 : characterUpdateListDtoV2.getCharacterUpdateDtoV2List()) {
                if (character.getCharacterName().equals(characterUpdateDtoV2.getCharacterName())) {
                    character.getCharacterDayContent().updateDayContent(characterUpdateDtoV2);

                    CharacterUpdateDtoV2 result = CharacterUpdateDtoV2.builder()
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
