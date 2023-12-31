package lostark.todo.domain.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "email", timeToLive = 3600)
public class Mail {

    @Id
    private Long id;

    @Indexed
    private String mail;

    private Integer number;

    private boolean check;

    private LocalDateTime regDate;

    public Mail(String mail, int number) {
        this.mail = mail;
        this.number = number;
        this.check = false;
        this.regDate = LocalDateTime.now();
    }
}
