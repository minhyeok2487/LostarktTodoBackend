package lostark.todo.global.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.exhandler.exceptions.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static lostark.todo.global.config.WebSecurityConfig.PERMIT_ALL_LINK;
import static lostark.todo.global.config.WebSecurityConfig.PERMIT_GET_LINK;

/**
 * JWT 인증 처리
 * HTTP 요청을 필터링하여 클라이언트에서 전달된 JWT 토큰 검증
 * 토큰이 유효한 경우 해당 토큰으로부터 추출한 사용자 정보를 사용하여 인증 완료
 * 인증된 사용자 정보를 'SecurityContextHolder' 에 저장하여 해당 요청을 이후 Spring Security 처리 단게에서 사용
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다.";
    private static final String AUTH_REQUIRED_MESSAGE = "인증이 필요한 서비스입니다.";
    private static final String ADMIN_REQUIRED_MESSAGE = "관리자 권한이 필요합니다.";
    private static final String AUTH_ERROR_MESSAGE = "인증 처리 중 오류가 발생했습니다.";

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException{
        // Authorization 헤더에서 Bearer 토큰 추출
        String token = parseBearerToken(request);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        try {
            // 토큰이 존재하면 검증 및 인증 컨텍스트 설정, 없으면 비인증 처리
            boolean proceed = (token != null && !token.equalsIgnoreCase("null"))
                    ? processToken(request, response, securityContext, token)
                    : processNoToken(request, response, securityContext);

            if (!proceed) {
                return; // 필터 체인 중단
            }

            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
        } catch (ServletException e) {
            log.error("Auth Error = {}", e.getMessage());
            sendErrorResponse(response, AUTH_ERROR_MESSAGE);
        } catch (RateLimitExceededException e) {
            sendRateLimitResponse(response, e.getMessage());
        }
    }

    private boolean processToken(HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext, String token) throws IOException {
        try {
            // 토큰 검증 후 인증 정보 구성
            String username = tokenProvider.validToken(token);
            if (!checkAdminRole(request, response, username)) {
                return false;
            }
            authenticateUser(request, securityContext, username);
            return true;
        } catch (Exception e) {
            log.debug("Invalid token: {}", e.getMessage());
            if (!isPermitAllPath(request)) {
                sendErrorResponse(response, INVALID_TOKEN_MESSAGE);
                return false;
            } else {
                setNullAuthentication(securityContext, request);
                return true;
            }
        }
    }

    private boolean processNoToken(HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext) throws IOException {
        if (!isPermitAllPath(request)) {
            // 인증이 필요한 경로인데 토큰이 없을 경우 즉시 에러 응답
            sendErrorResponse(response, AUTH_REQUIRED_MESSAGE);
            return false;
        } else {
            setNullAuthentication(securityContext, request);
            return true;
        }
    }

    private void authenticateUser(HttpServletRequest request, SecurityContext securityContext, String username) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);
    }

    private boolean checkAdminRole(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        if (request.getRequestURI().startsWith("/admin")) {
            // /admin 경로 접근 시 관리자 권한 검증
            Member member = memberService.get(username);
            if (member.getRole() != Role.ADMIN) {
                sendErrorResponse(response, ADMIN_REQUIRED_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void setNullAuthentication(SecurityContext securityContext, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, null, AuthorityUtils.NO_AUTHORITIES);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);
    }

    private boolean isPermitAllPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // permitAll 경로 또는 GET 전용 허용 경로 여부 확인
        return checkPatternMatch(path, PERMIT_ALL_LINK) || ("GET".equals(method) && checkPatternMatch(path, PERMIT_GET_LINK));
    }

    private boolean checkPatternMatch(String path, String[] patterns) {
        for (String pattern : patterns) {
            if (pattern.endsWith("/**")) {
                String basePattern = pattern.substring(0, pattern.length() - 3);
                if (path.startsWith(basePattern)) {
                    return true;
                }
            } else if (path.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        if (response != null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            String jsonResponse = String.format("{\"message\": \"%s\"}", message);
            response.getWriter().write(jsonResponse);
        } else {
            log.error(message); // 응답 객체가 없는 경우 로그로 기록
        }
    }

    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"errorMessage\": \"%s\"}", message);
        response.getWriter().write(jsonResponse);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
