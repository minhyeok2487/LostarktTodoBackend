package lostark.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 스프링 빈으로 등록
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대하여
        registry.addMapping("/**")
                // Origin이 http:localhost:3000에 대해.
                .allowedOrigins("http://localhost:3000", // 로컬
                        "https://app.loatodo.com/", "https://loatodo.com", "https://www.loatodo.com", "http://192.168.35.174:3000", "http://210.221.60.15:3000",
                        "https://dev.loatodo.com") // 배포
                // GET, POST, PUT, PATCH, DELETE, OPTIONS 메서드를 허용한다.
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }

}
