package lostark.todo.domain.todo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterDayContent;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.member.Member;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private long id;

    @Column(nullable = false)
    private TodoContentName contentName;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "character_id")
    @JsonBackReference //순환참조 방지
    private Character character;

    public Todo updateCheck(boolean check) {
        this.isChecked = !check;
        return this;
    }
}
