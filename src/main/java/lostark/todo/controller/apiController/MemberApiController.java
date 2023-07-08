package lostark.todo.controller.apiController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public Member signup(Member member) {
        Member signupMember = memberService.signup(member);
        return signupMember;
    }
}
