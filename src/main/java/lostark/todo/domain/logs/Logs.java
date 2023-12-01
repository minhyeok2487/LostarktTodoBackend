package lostark.todo.domain.logs;

import lombok.*;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;

import javax.persistence.*;

@Getter
@Table(name = "logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Logs extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logs_id")
    private long id;

    private long memberId;

    private String message;
}
