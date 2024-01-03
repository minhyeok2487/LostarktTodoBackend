package lostark.todo.domain.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "user")
public class User {

    @Id
    private Long id;

    @Indexed
    private String username;

    private LocalDateTime date;

    public User(String username) {
        this.username = username;
        this.date = LocalDateTime.now();
    }
}
