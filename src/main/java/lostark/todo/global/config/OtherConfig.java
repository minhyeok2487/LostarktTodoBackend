package lostark.todo.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ConcurrentHashMap;

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

//    @Bean
//    @Primary
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // 기본 스레드 수
//        executor.setMaxPoolSize(10); // 최대 스레드 수
//        executor.setQueueCapacity(100); // 대기 큐 크기
//        executor.setThreadNamePrefix("LogAsync-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 큐 꽉 차면 호출자 스레드 실행
//        executor.initialize();
//        return executor;
//    }
}
