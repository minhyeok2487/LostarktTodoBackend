package lostark.todo.service.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v1.dto.memberDto.MemberSignupDtoV1;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Member signup(MemberSignupDtoV1 signupDto, List<Character> characterList) {
        if(memberRepository.existsByUsername(signupDto.getUsername())) {
            String errorMessage = signupDto.getUsername() + " 이미 존재하는 username 입니다.";
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        Member member = Member.builder()
                .username(signupDto.getUsername())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .apiKey(signupDto.getApiKey())
                .role(Role.USER)
                .build();
        characterList.stream().map(o -> member.addCharacter(o)).collect(Collectors.toList());

        return memberRepository.save(member);
    }

    public Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(username + "은(는) 없는 회원 입니다."));
    }
}
