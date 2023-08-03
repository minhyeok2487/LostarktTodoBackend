package lostark.todo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security 웹 애플리케이션 보안 구성
 */
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 인증 필요하지 않는 링크
     * swagger, auth
     */
    private static final String[] PERMIT_ALL_LINK = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/auth/**"
    };

    /**
     * 보안 설정
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                .anyRequest().authenticated(); // 나머지 경로 모두 인증

        /**
         * filter 등록.
         * 매 요청마다
         * CorsFilter 실행한 후에
         * jwtAuthenticationFilter 실행
         */
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
    }
}
