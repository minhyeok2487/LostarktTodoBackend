package lostark.todo.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.Role;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.member.service.MemberService;
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

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청에서 토큰 가져오기
            String token = parseBearerToken(request);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

            if (token != null && !token.equalsIgnoreCase("null")) {
                try {
                    // username 값 가져옴. 위조된 경우 예외 처리
                    String username = tokenProvider.validToken(token);

                    // 인증 완료
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authentication);

                    // Admin 권한 체크
                    if (request.getRequestURI().startsWith("/admin")) {
                        Member member = memberService.get(username);
                        if (member.getRole() != Role.ADMIN) {
                            sendErrorResponse(response, "관리자 권한이 필요합니다.");
                            return;
                        }
                    }
                } catch (Exception e) {
                    log.debug("Invalid token: {}", e.getMessage());
                    if (!isPermitAllPath(request)) {
                        sendErrorResponse(response, "유효하지 않은 토큰입니다.");
                        return;
                    }
                    // 인증이 필요없는 경로면 null로 설정
                    setNullAuthentication(securityContext, request);
                }
            } else {
                // 토큰이 없는 경우
                if (!isPermitAllPath(request)) {
                    sendErrorResponse(response, "인증이 필요한 서비스입니다.");
                    return;
                }
                // 인증이 필요없는 경로면 null로 설정
                setNullAuthentication(securityContext, request);
            }

            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Auth Error = {}", e.getMessage());
            sendErrorResponse(response, "인증 처리 중 오류가 발생했습니다.");
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

        // 모든 메소드 허용 패턴 체크
        if (checkPatternMatch(path, PERMIT_ALL_LINK)) {
            return true;
        }

        // GET 메소드 전용 패턴 체크
        return "GET".equals(method) && checkPatternMatch(path, PERMIT_GET_LINK);
    }

    private boolean checkPatternMatch(String path, String[] patterns) {
        for (String pattern : patterns) {
            if (pattern.endsWith("/**")) {
                // /** 패턴 처리
                String basePattern = pattern.substring(0, pattern.length() - 3);
                if (path.startsWith(basePattern)) {
                    return true;
                }
            } else {
                // 정확한 경로 매칭
                if (path.equals(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"message\": \"%s\"}", message);
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