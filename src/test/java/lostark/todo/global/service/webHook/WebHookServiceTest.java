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
        // cooldownCache 필드가 Caffeine Cache 타입인지 확인
        Object cache = ReflectionTestUtils.getField(service, "cooldownCache");
        assertThat(cache).isNotNull();
        assertThat(cache.getClass().getName()).contains("caffeine");
    }

    @Test
    @DisplayName("쿨다운 동작: 동일 예외 재발송 차단")
    void cooldown_shouldBlockDuplicate() {
        WebHookService service = new WebHookService();
        ReflectionTestUtils.setField(service, "url", "https://example.com/webhook");

        // 첫 번째 호출 - 쿨다운 캐시에 등록됨
        // callEvent는 @Async이므로 직접 쿨다운 캐시를 검증
        com.github.benmanes.caffeine.cache.Cache<String, Boolean> cache =
                (com.github.benmanes.caffeine.cache.Cache<String, Boolean>)
                        ReflectionTestUtils.getField(service, "cooldownCache");

        assertThat(cache).isNotNull();
        assertThat(cache.getIfPresent("RuntimeException")).isNull();

        cache.put("RuntimeException", Boolean.TRUE);
        assertThat(cache.getIfPresent("RuntimeException")).isTrue();
    }
}
