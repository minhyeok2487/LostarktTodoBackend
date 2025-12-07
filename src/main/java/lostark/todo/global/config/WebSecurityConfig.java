package lostark.todo.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.config.filter.JwtAuthenticationFilter;
import lostark.todo.global.config.filter.MyGameApiKeyFilter;
import lostark.todo.global.security.CustomOAuth2UserService;
import lostark.todo.global.security.OAuthSuccessHandler;
import lostark.todo.global.security.RedirectUrlCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security 웹 애플리케이션 보안 구성
 */
@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MyGameApiKeyFilter myGameApiKeyFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final RedirectUrlCookieFilter redirectUrlFilter;

    /**
     * 인증 필요하지 않는 링크
     * swagger, auth
     */
    public static final String[] PERMIT_ALL_LINK = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/auth/**", "/oauth2/**",
            "/css/**", "js/**", "/",
            "/v3/mail/**", "/v3/notification/**",
            "/v3/auth/signup", "/v3/auth/character", "/v3/auth/login",
            "/v2/boards/**","/v3/boards/**", "/v2/comments", "/v3/home/test", "/auth/authorize",
            "/login/oauth2/**",
            "/api/v1/auth/signup", "/api/v1/member/character", "/api/v1/auth/login", "/api/v1/mail/**",
            "/manage/health", "/manage/info", "/manage/prometheus",
            "/api/v1/games/**", "/api/v1/events/**"  // MyGame API - API Key 필터가 별도로 처리
    };

    public static final String[] PERMIT_GET_LINK = {
        "/v3/comments", "/api/v1/recruting-board/**", "/api/v1/community/**",
    };


    /**
     * 보안 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http.cors() // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정.
                .and()
                .csrf() //csrf는 현재 사용하지 않으므로 disable
                .disable()
                .httpBasic() // token을 사용하므로 basic 인증 disable
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 기반이 아님을 선언
                .and()
                .authorizeRequests() // 인증 필요하지 않는 링크
                .antMatchers(PERMIT_ALL_LINK).permitAll()
                .antMatchers(HttpMethod.GET, PERMIT_GET_LINK).permitAll()
                .anyRequest().authenticated() // 나머지 경로 모두 인증
                .and().oauth2Login()//OAuth 로그인
                .authorizationEndpoint().baseUri("/auth/authorize")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuthSuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()); // 403에러 리턴

        /*
         * filter 등록.
         * 매 요청마다
         * CorsFilter 실행한 후에
         * jwtAuthenticationFilter 실행
         */
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        // MyGame API Key 필터 (JWT 필터 이전에 실행)
        http.addFilterBefore(myGameApiKeyFilter, JwtAuthenticationFilter.class);

        //리다이렉트되기 전에 필터 실행
        http.addFilterBefore(redirectUrlFilter, OAuth2AuthorizationRequestRedirectFilter.class);
        return http.build();
    }
}
