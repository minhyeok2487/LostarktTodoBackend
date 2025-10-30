package lostark.todo.domain.generaltodo.entity;

import lombok.*;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "general_todo_item")
public class GeneralTodoItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_todo_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "folder_id")
    private GeneralTodoFolder folder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private GeneralTodoCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean completed;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void updateCompleted(boolean completed) {
        this.completed = completed;
    }

    public void moveTo(GeneralTodoFolder folder, GeneralTodoCategory category) {
        this.folder = folder;
        this.category = category;
    }
}
