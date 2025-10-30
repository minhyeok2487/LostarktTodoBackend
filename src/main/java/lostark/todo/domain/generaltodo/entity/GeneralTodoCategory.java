package lostark.todo.domain.generaltodo.entity;

import lombok.*;
import lostark.todo.domain.generaltodo.enums.GeneralTodoViewMode;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "general_todo_category")
public class GeneralTodoCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_todo_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "folder_id")
    private GeneralTodoFolder folder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 7)
    private String color;

    @Column(nullable = false)
    private int sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("'LIST'")
    @Builder.Default
    private GeneralTodoViewMode viewMode = GeneralTodoViewMode.LIST;

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneralTodoItem> items = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void updateViewMode(GeneralTodoViewMode viewMode) {
        this.viewMode = viewMode;
    }
}
