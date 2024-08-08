package lostark.todo.service;

import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static lostark.todo.Constant.TEST_USERNAME;
import static lostark.todo.constants.ErrorMessages.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;


    @Test
    @DisplayName("이메일 인증 후 회원가입 성공")
    void createMember_Success() {
        // given
        String email = "test@example.com";
        String password = "password123";

        // when
        Member createdMember = memberService.createMember(email, password);

        // then
        Assertions.assertThat(createdMember.getUsername()).isEqualTo(email);
        Assertions.assertThat(createdMember.getPassword()).isNotEqualTo(password);
        Assertions.assertThat(createdMember.getAuthProvider()).isEqualTo("none");
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void createMember_Fail_DuplicateEmail() {
        // given
        String email = "repeat2487@gmail.com";
        String password = "password123";

        // when
        Throwable thrown = Assertions.catchThrowable(() -> memberService.createMember(email, password));

        // then
        Assertions.assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("대표캐릭터 변경 성공")
    void editMainCharacter_Success() {
        // given
        String username = "repeat2487@gmail.com";
        String mainCharacter = "소울볼링";

        // when
        memberService.editMainCharacter(username, mainCharacter);

        // then
        Member updatedMember = memberRepository.get(username).orElseThrow();
        Assertions.assertThat(updatedMember.getMainCharacter()).isEqualTo(mainCharacter);
    }

    @Test
    @DisplayName("대표캐릭터 변경 실패 - 회원에 가입되지 않은 캐릭터")
    void editMainCharacter_Fail_NullCharacter() {
        // given
        String username = "repeat2487@gmail.com";
        String mainCharacter = "소울볼링123123";

        // when
        Throwable thrown = Assertions.catchThrowable(() -> memberService.editMainCharacter(username, mainCharacter));

        // then
        Assertions.assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MEMBER_CHARACTER_NOT_FOUND);
    }

    @Test
    @DisplayName("소셜 회원 -> 일반 회원 전환 성공")
    void editProvider_Success() {
        // given
        String username = "repeat2487@gmail.com";
        String newPassword = "1234";

        // when
        memberService.editProvider(username, newPassword);

        // then
        Member updatedMember = memberRepository.get(username).orElseThrow();
        Assertions.assertThat(updatedMember.getAuthProvider()).isEqualTo("none");
        Assertions.assertThat(updatedMember.getPassword()).isNotEqualTo(newPassword);
    }

    @Test
    @DisplayName("소셜 회원 -> 일반 회원 전환 실패 - 테스트 회원")
    void editProvider_Fail_TestMember() {
        // given
        String newPassword = "1234";

        // when
        Throwable thrown = Assertions.catchThrowable(() -> memberService.editProvider(TEST_USERNAME, newPassword));

        // then
        Assertions.assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_MEMBER_NOT_ACCESS);
    }

    @Test
    @DisplayName("소셜 회원 -> 일반 회원 전환 실패 - 소셜 회원 아님")
    void editProvider_Fail_Not_SocialMember() {
        // given
        String username = "repeater2487@naver.com";
        String newPassword = "1234";

        // when
        Throwable thrown = Assertions.catchThrowable(() -> memberService.editProvider(username, newPassword));

        // then
        Assertions.assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MEMBER_NOT_SOCIAL);
    }
}
