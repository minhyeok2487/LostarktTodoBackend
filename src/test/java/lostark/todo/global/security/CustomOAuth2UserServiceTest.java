package lostark.todo.global.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomOAuth2UserServiceTest {

    @Test
    @DisplayName("클래스 레벨 @Transactional이 제거되었다 (외부 HTTP 호출 중 커넥션 점유 방지)")
    void shouldNotHaveClassLevelTransactional() {
        // javax.transaction.Transactional
        Transactional javaxAnnotation = CustomOAuth2UserService.class.getAnnotation(Transactional.class);
        assertThat(javaxAnnotation).isNull();

        // org.springframework.transaction.annotation.Transactional
        org.springframework.transaction.annotation.Transactional springAnnotation =
                CustomOAuth2UserService.class.getAnnotation(org.springframework.transaction.annotation.Transactional.class);
        assertThat(springAnnotation).isNull();
    }
}
