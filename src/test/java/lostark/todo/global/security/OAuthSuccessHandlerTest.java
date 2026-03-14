package lostark.todo.global.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthSuccessHandlerTest {

    @Test
    @DisplayName("클래스 레벨 @Transactional이 제거되었다 (읽기 전용 → 불필요한 커넥션 점유 방지)")
    void shouldNotHaveClassLevelTransactional() {
        Transactional javaxAnnotation = OAuthSuccessHandler.class.getAnnotation(Transactional.class);
        assertThat(javaxAnnotation).isNull();

        org.springframework.transaction.annotation.Transactional springAnnotation =
                OAuthSuccessHandler.class.getAnnotation(org.springframework.transaction.annotation.Transactional.class);
        assertThat(springAnnotation).isNull();
    }
}
