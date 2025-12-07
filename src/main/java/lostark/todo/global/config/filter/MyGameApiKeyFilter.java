package lostark.todo.global.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MyGame API 전용 API Key 인증 필터
 * /api/v1/games, /api/v1/events 경로에 대해 Bearer 토큰으로 전달된 API Key 검증
 */
@Slf4j
@Component
public class MyGameApiKeyFilter extends OncePerRequestFilter {

    private static final String INVALID_API_KEY_MESSAGE = "유효하지 않은 API Key입니다.";
    private static final String API_KEY_REQUIRED_MESSAGE = "API Key가 필요합니다.";

    @Value("${MYGAME_API_KEY:}")
    private String myGameApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // MyGame API 경로가 아니면 필터 통과
        if (!isMyGameApiPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // API Key가 설정되지 않았으면 경고 로그 출력 후 통과 (개발 환경 고려)
        if (!StringUtils.hasText(myGameApiKey)) {
            log.warn("MYGAME_API_KEY가 설정되지 않았습니다. 보안을 위해 환경 변수를 설정하세요.");
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 Bearer 토큰 추출
        String apiKey = parseBearerToken(request);

        if (!StringUtils.hasText(apiKey)) {
            sendErrorResponse(response, API_KEY_REQUIRED_MESSAGE);
            return;
        }

        // API Key 검증
        if (!myGameApiKey.equals(apiKey)) {
            sendErrorResponse(response, INVALID_API_KEY_MESSAGE);
            return;
        }

        // 검증 통과
        filterChain.doFilter(request, response);
    }

    private boolean isMyGameApiPath(String path) {
        return path.startsWith("/api/v1/games") || path.startsWith("/api/v1/events");
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"success\": false, \"error\": {\"message\": \"%s\"}}", message);
        response.getWriter().write(jsonResponse);
    }
}
