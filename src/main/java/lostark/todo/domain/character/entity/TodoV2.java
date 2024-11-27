package lostark.todo.domain.character.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.util.content.entity.WeekContent;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TodoV2 extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id")
    @JsonBackReference
    private WeekContent weekContent;

    @Column(nullable = false)
    private boolean isChecked;

    private int gold; //골드

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    @JsonBackReference //순환참조 방지
    private Character character;

    private int coolTime;

    @ColumnDefault("0")
    private int sortNumber;

    @ColumnDefault("false")
    private boolean goldCheck;

    public void updateMessage(String message) {
        this.message = StringUtils.hasText(message) ? message : null;
    }

    public void updateWeekContent(WeekContent weekContent) {
        this.weekContent = weekContent;
        this.gold = weekContent.getGold();
    }

    @Override
    public String toString() {
        return "TodoV2{" +
                "id=" + id +
                ", weekContent=" + weekContent.getId() +
                ", isChecked=" + isChecked +
                ", gold=" + gold +
                ", message='" + message + '\'' +
                ", character=" + character.getId() +
                ", coolTime=" + coolTime +
                '}';
    }

    public void updateGoldCheck() {
        this.goldCheck = !this.goldCheck;
    }
}
