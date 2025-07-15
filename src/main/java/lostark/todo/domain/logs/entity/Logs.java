package lostark.todo.domain.logs.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "logs", indexes = {
        @Index(name = "idx_logs_get", columnList = "memberId, characterId, logContent, localDate, name"), // Get Index
        @Index(name = "idx_logs_search_filter", columnList = "memberId, deleted, characterId, logContent, lastModifiedDate DESC") // Search Index Filter
})
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

    private String name;

    private String message;

    private double profit;

    @ColumnDefault("false")
    private boolean deleted;

    public void updateFrom(Logs newLog) {
        this.deleted = newLog.isDeleted();
        this.message = newLog.getMessage();
        this.profit = newLog.getProfit();
        this.localDate = newLog.getLocalDate();
    }

    public static String createEtcMessage(lostark.todo.domain.character.entity.Character character, String actionMessage, double profit) {
        String verb = (profit < 0) ? "소모했습니다." : "획득했습니다.";
        return String.format("%s 서버의 %s(%s) 캐릭터가 %s %.0f골드를 %s",
                character.getServerName(),
                character.getCharacterName(),
                character.getItemLevel(),
                actionMessage,
                Math.abs(profit),
                verb);
    }
}
