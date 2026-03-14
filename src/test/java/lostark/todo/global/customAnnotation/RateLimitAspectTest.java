package lostark.todo.global.customAnnotation;

import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @InjectMocks
    private RateLimitAspect rateLimitAspect;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RateLimit rateLimit;

    @BeforeEach
    void setUp() {
        given(cacheManager.getCache("rateLimitCache")).willReturn(cache);
    }

    @Test
    @DisplayName("첫 요청은 정상 처리된다")
    void firstRequest_shouldProceed() throws Throwable {
        // given
        given(joinPoint.getArgs()).willReturn(new Object[]{"testUser"});
        given(cache.get(anyString())).willReturn(null);
        given(joinPoint.proceed()).willReturn("success");

        // when
        Object result = rateLimitAspect.checkRateLimit(joinPoint, rateLimit);

        // then
        assertThat(result).isEqualTo("success");
        verify(cache).put(anyString(), eq("1"));
    }

    @Test
    @DisplayName("중복 요청은 차단된다")
    void duplicateRequest_shouldBeBlocked() {
        // given
        given(joinPoint.getArgs()).willReturn(new Object[]{"testUser"});
        given(cache.get(anyString())).willReturn(() -> "1");
        given(rateLimit.value()).willReturn(10);

        // when & then
        assertThatThrownBy(() -> rateLimitAspect.checkRateLimit(joinPoint, rateLimit))
                .isInstanceOf(ConditionNotMetException.class)
                .hasMessageContaining("10초 후 재요청이 가능합니다.");
    }

    @Test
    @DisplayName("Timer/ScheduledExecutor 없이 캐시 TTL로만 동작한다")
    void shouldNotUseTimerOrScheduler() throws Throwable {
        // given
        given(joinPoint.getArgs()).willReturn(new Object[]{"user"});
        given(cache.get(anyString())).willReturn(null);
        given(joinPoint.proceed()).willReturn("ok");

        int initialThreadCount = Thread.activeCount();

        // when - 100번 반복해도 스레드 증가 없어야 함
        for (int i = 0; i < 100; i++) {
            given(joinPoint.getArgs()).willReturn(new Object[]{"user" + i});
            rateLimitAspect.checkRateLimit(joinPoint, rateLimit);
        }

        // then - 스레드 추가 없음 (Timer/Scheduler 사용하지 않으므로)
        int afterThreadCount = Thread.activeCount();
        assertThat(afterThreadCount - initialThreadCount).isLessThan(3);
    }

    @Test
    @DisplayName("캐시가 null이면 바로 proceed한다")
    void nullCache_shouldProceedDirectly() throws Throwable {
        // given
        given(cacheManager.getCache("rateLimitCache")).willReturn(null);
        given(joinPoint.getArgs()).willReturn(new Object[]{"user"});
        given(joinPoint.proceed()).willReturn("ok");

        // when
        Object result = rateLimitAspect.checkRateLimit(joinPoint, rateLimit);

        // then
        assertThat(result).isEqualTo("ok");
    }
}
