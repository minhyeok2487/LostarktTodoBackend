package lostark.todo.domain.generaltodo.entity;

import lombok.*;
import lostark.todo.domain.generaltodo.enums.GeneralTodoStatusType;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "general_todo_status")
public class GeneralTodoStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_todo_status_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private GeneralTodoCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GeneralTodoStatusType type;

    @Builder.Default
    @OneToMany(mappedBy = "status")
    private List<GeneralTodoItem> items = new ArrayList<>();

    public boolean isDoneType() {
        return type.isDone();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
