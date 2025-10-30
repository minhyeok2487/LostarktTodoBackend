package lostark.todo.domain.generaltodo.entity;

import lombok.*;
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
@Table(name = "general_todo_folder")
public class GeneralTodoFolder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_todo_folder_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int sortOrder;

    @Builder.Default
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneralTodoCategory> categories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneralTodoItem> items = new ArrayList<>();

    public void rename(String name) {
        this.name = name;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
