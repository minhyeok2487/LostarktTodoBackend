package lostark.todo.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.*;

@Configuration
@RequiredArgsConstructor
public class OtherConfig {


    // 회원 중복 가입 방지 HashMap
    @Bean
    public ConcurrentHashMap<String, Boolean> usernameLocks() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "inspectionExecutor", destroyMethod = "shutdown")
    public ExecutorService inspectionExecutor() {
        return new ThreadPoolExecutor(
                4, 8, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
