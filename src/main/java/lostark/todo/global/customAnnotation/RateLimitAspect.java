package lostark.todo.global.customAnnotation;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final CacheManager cacheManager;

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // username 가져오기
        String username = extractUsername(joinPoint);

        String key = "rate:limit:" + username;
        Cache cache = cacheManager.getCache("rateLimitCache");

        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                throw new IllegalStateException(rateLimit + "초 후 재요청이 가능합니다.");
            }

            // 캐시에 저장
            cache.put(key, "1");

            try {
                return joinPoint.proceed();
            } finally {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cache.evict(key);
                    }
                }, rateLimit.value() * 1000L);
            }
        }

        return joinPoint.proceed();
    }

    private String extractUsername(ProceedingJoinPoint joinPoint) {
        // 1. 메서드 파라미터에서 직접 username 찾기
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof String) {
                return (String) arg;
            }
        }

        // 2. Spring Security Context에서 찾기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        throw new IllegalStateException("사용자를 찾을 수 없습니다.");
    }
}
