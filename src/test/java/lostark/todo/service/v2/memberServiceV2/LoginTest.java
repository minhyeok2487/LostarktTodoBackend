package lostark.todo.service.v2.memberServiceV2;

import groovy.util.logging.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v2.LostarkCharacterServiceV2;
import lostark.todo.service.v2.MemberServiceV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Slf4j
class LoginTest {

    @Autowired
    LostarkCharacterServiceV2 lostarkCharacterService;

    @Autowired
    MemberServiceV2 memberService;

    @Autowired
    Validator validator;

    String username;

    String password;

    MemberLoginDto memberLoginDto;

    @BeforeEach
    void init() {
        username = "test";
        password = "1234";
        memberLoginDto = MemberLoginDto.builder()
                .username(username)
                .password(password)
                .build();
    }

    @Test
    @DisplayName("login 테스트 성공")
    void login() {
        // when
        Member loginMember = memberService.login(memberLoginDto);

        // then
        assertThat(memberLoginDto.getUsername()).isEqualTo(loginMember.getUsername());
        assertThat(memberLoginDto.getPassword()).isNotEqualTo(loginMember.getPassword());
    }

    @Test
    @DisplayName("login 테스트 실패 : not equal password")
    void loginNotEqualPassword() {
        // when
        memberLoginDto.setPassword("111");

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.login(memberLoginDto);
        });
    }

    @Test
    @DisplayName("createMember 테스트 실패: @Valid Error")
    void createMemberNotEmpty() {
        // when
        memberLoginDto.setUsername("");
        Set<ConstraintViolation<MemberLoginDto>> validation = validator.validate(memberLoginDto);

        // then
        Iterator<ConstraintViolation<MemberLoginDto>> iterator = validation.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<MemberLoginDto> next = iterator.next();
            String message = next.getPropertyPath() + " (은)는 " + next.getMessage();
            messages.add(message);
            System.out.println("message = " + message);
        }

        Assertions.assertThat(messages).contains("username (은)는 비어 있을 수 없습니다");
    }
}