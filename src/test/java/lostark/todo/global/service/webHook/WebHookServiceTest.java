package lostark.todo.global.service.webHook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class WebHookServiceTest {

    @Test
    @DisplayName("ConcurrentHashMap 대신 Caffeine cache를 사용한다")
    void shouldUseCaffeineCache() {
        WebHookService service = new WebHookService();
        Object cache = ReflectionTestUtils.getField(service, "cooldownCache");
        assertThat(cache).isNotNull();
        assertThat(cache.getClass().getName()).contains("caffeine");
    }

    @Test
    @DisplayName("첫 번째 호출은 전송을 허용한다")
    void shouldSendNotification_firstCall() {
        WebHookService service = new WebHookService();
        Exception ex = new RuntimeException("test error");

        assertThat(service.shouldSendNotification(ex)).isTrue();
    }

    @Test
    @DisplayName("동일 예외 타입의 두 번째 호출은 쿨다운으로 차단한다")
    void shouldBlockDuplicate_sameExceptionType() {
        WebHookService service = new WebHookService();
        Exception ex = new RuntimeException("test error");

        assertThat(service.shouldSendNotification(ex)).isTrue();
        assertThat(service.shouldSendNotification(ex)).isFalse();
    }

    @Test
    @DisplayName("다른 예외 타입은 별도로 허용한다")
    void shouldAllow_differentExceptionType() {
        WebHookService service = new WebHookService();

        assertThat(service.shouldSendNotification(new RuntimeException("error1"))).isTrue();
        assertThat(service.shouldSendNotification(new IllegalArgumentException("error2"))).isTrue();
    }

    @Test
    @DisplayName("제외 키워드가 포함된 예외는 전송하지 않는다")
    void shouldExclude_keywordMatch() {
        WebHookService service = new WebHookService();

        assertThat(service.shouldSendNotification(new RuntimeException("로스트아크 서버가 점검중 입니다."))).isFalse();
        assertThat(service.shouldSendNotification(new RuntimeException("올바르지 않은 apiKey 입니다."))).isFalse();
    }
}
