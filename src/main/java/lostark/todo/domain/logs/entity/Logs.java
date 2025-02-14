package lostark.todo.domain.logs.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Logs extends BaseTimeEntity {

    // 로그 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logs_id")
    private long id;

    private LocalDate localDate;

    private long memberId;

    private long characterId;

    @Enumerated(EnumType.STRING)
    private LogType logType;

    @Enumerated(EnumType.STRING)
    private LogContent logContent;

    private String message;

    private double profit;
}
