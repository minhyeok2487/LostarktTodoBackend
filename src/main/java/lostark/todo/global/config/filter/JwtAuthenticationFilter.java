package lostark.todo.global.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
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
 * 
 * Updated to handle JJWT 0.12.6 exceptions
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다.";
    private static final String AUTH_REQUIRED_MESSAGE = "인증이 필요한 서비스입니다.";
    private static final String ADMIN_REQUIRED_MESSAGE = "관리자 권한이 필요합니다.";
    private static final String AUTH_ERROR_MESSAGE = "인증 처리 중 오류가 발생했습니다.";
    private static final String TOKEN_EXPIRED_MESSAGE = "토큰이 만료되었습니다.";

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String token = parseBearerToken(request);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        try {
            if (token != null && !token.equalsIgnoreCase("null")) {
                processToken(request, response, securityContext, token);
            } else {
                processNoToken(request, response, securityContext);
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

    private void processToken(HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext, String token) throws IOException {
        try {
            String username = tokenProvider.validToken(token);
            authenticateUser(request, securityContext, username);
            checkAdminRole(request, username);
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token: {}", e.getMessage());
            if (!isPermitAllPath(request)) {
                sendErrorResponse(response, TOKEN_EXPIRED_MESSAGE);
            } else {
                setNullAuthentication(securityContext, request);
            }
        } catch (SecurityException | MalformedJwtException e) {
            // Handles signature verification errors and malformed tokens
            log.debug("Invalid JWT signature/format: {}", e.getMessage());
            if (!isPermitAllPath(request)) {
                sendErrorResponse(response, INVALID_TOKEN_MESSAGE);
            } else {
                setNullAuthentication(securityContext, request);
            }
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT token: {}", e.getMessage());
            if (!isPermitAllPath(request)) {
                sendErrorResponse(response, INVALID_TOKEN_MESSAGE);
            } else {
                setNullAuthentication(securityContext, request);
            }
        } catch (Exception e) {
            log.debug("Invalid token: {}", e.getMessage());
            if (!isPermitAllPath(request)) {
                sendErrorResponse(response, INVALID_TOKEN_MESSAGE);
            } else {
                setNullAuthentication(securityContext, request);
            }
        }
    }

    private void processNoToken(HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext) throws IOException {
        if (!isPermitAllPath(request)) {
            sendErrorResponse(response, AUTH_REQUIRED_MESSAGE);
        } else {
            setNullAuthentication(securityContext, request);
        }
    }

    private void authenticateUser(HttpServletRequest request, SecurityContext securityContext, String username) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);
    }

    private void checkAdminRole(HttpServletRequest request, String username) throws IOException {
        if (request.getRequestURI().startsWith("/admin")) {
            Member member = memberService.get(username);
            if (member.getRole() != Role.ADMIN) {
                sendErrorResponse(null, ADMIN_REQUIRED_MESSAGE);
            }
        }
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
            String jsonResponse = String.format("{\\\"message\\\": \\\"%s\\\"}", message);
            response.getWriter().write(jsonResponse);
        } else {
            log.error(message); // 응답 객체가 없는 경우 로그로 기록
        }
    }

    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\\\"errorMessage\\\": \\\"%s\\\"}", message);
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