package lostark.todo.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class OtherConfig {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * 회원 중복 가입 방지 HashMap
     */
    @Bean
    public ConcurrentHashMap<String, Boolean> usernameLocks() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    AuditReader auditReader() {
        return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
    }
}
