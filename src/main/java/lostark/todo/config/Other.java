package lostark.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Other {

    /**
     * 회원 중복 가입 방지
     */
    @Bean
    public ConcurrentHashMap<String, Boolean> usernameLocks() {
        return new ConcurrentHashMap<>();
    }
}
