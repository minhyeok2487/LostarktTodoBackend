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

//    @Autowired
//    MemberService memberService;
//
//    @Test
//    @Rollback(value = false)
//    void resetTestUser() {
//        long memberId = 5726L;
//        String username = "test@test.com";
//        Member member = memberService.findMember(memberId);
//        memberService.removeUser(member);
//    }
}
