package lostark.todo;

import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
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

    @Autowired
    CharacterService characterService;
    @Test
    @Rollback(value = false)
    void resetTestUser() {
//        long memberId = 5726L;
//        String username = "repeater2487@naver.com";
//        Member member = memberService.findMember(username);
//        memberService.removeUser(member);
//        characterService.removeUser(member);
    }
}
