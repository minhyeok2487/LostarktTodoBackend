package lostark.todo;

import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class reset {

    @Autowired
    MemberService memberService;

    @Test
    @Rollback(value = false)
    void resetTestUser() {
        String username = "repeater2487@naver.com";
        Member member = memberService.findMember(username);
        memberService.removeUser(member);
    }
}
