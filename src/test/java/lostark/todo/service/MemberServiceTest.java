package lostark.todo.service;

import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Value("Lostark-API-Key")
    String apiKey;

    @Test
    void findMemberTest() {
        // 회원가입
        String username = "testUser";
        String password = "123456";
        MemberSignupDto memberSignupDto = new MemberSignupDto(username, password, apiKey);
        Member signupMember = memberService.signup(memberSignupDto);
        System.out.println("signupMember = " + signupMember.getUsername());


        Member findMember = memberService.findMember(username);
        System.out.println("findMember = " + findMember.getUsername());

    }
}