package lostark.todo.domain.customTodo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
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
public class CustomTodo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_todo_id")
    private long id;

    @Column(nullable = false)
    private String contentName;

    @Column(nullable = false)
    private boolean isChecked;

    @Enumerated(EnumType.STRING)
    private CustomTodoFrequencyEnum frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    @JsonBackReference //순환참조 방지
    private Character character;

    public void check() {
        this.isChecked = !this.isChecked;
    }

    public void update(String contentName) {
        this.contentName = contentName;
    }
}
