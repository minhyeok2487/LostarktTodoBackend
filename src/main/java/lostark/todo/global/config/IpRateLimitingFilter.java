package lostark.todo.global.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lostark.todo.global.exhandler.exceptions.RateLimitExceededException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitingFilter implements Filter {

    // IP별로 버킷을 저장하는 ConcurrentHashMap
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // GET 요청과 OPTIONS 요청은 Rate Limiting을 적용하지 않음
        if ("GET".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String clientIp = getClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

        // 토큰이 있으면 요청 허용, 없으면 429 응답
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("많은 요청이 있습니다. 잠시후 다시 요청해주세요.");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // 1. 일반 사용자를 위한 요청 제한: 10초 8회
    // 2. 공격 방지를 위한 제한: 1분 50회
    private Bucket createNewBucket(String key) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(8, Refill.greedy(8, Duration.ofSeconds(10))))
                .addLimit(Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1))))
                .build();
    }


    // 클라이언트 IP 추출
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}