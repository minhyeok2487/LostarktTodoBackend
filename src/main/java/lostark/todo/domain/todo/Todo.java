package lostark.todo.domain.todo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.character.Character;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private long id;

    @Column(nullable = false)
    private String contentName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String weekCategory;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private boolean isChecked;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    @JsonBackReference //순환참조 방지
    private Character character;

    public Todo updateCheck() {
//        this.isChecked = !check;
        this.isChecked = !this.isChecked;
        return this;
    }

    public Todo updateContent(WeekContentDto weekContentDto) {
        this.name = weekContentDto.getName();
        this.gold = weekContentDto.getGold();
        return this;
    }

    public Todo updateMessage(String message) {
        this.message = message;
        return this;
    }
}
