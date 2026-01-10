package lostark.todo.domain.servertodo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.EnumSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "server_todo")
public class ServerTodo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "server_todo_id")
    private Long id;

    @Column(nullable = false)
    private String contentName;

    @Builder.Default
    @Column(nullable = false)
    private boolean defaultEnabled = true;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "server_todo_visible_weekday",
            joinColumns = @JoinColumn(name = "server_todo_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "visible_weekday", nullable = false, length = 10)
    private Set<VisibleWeekday> visibleWeekdays = EnumSet.noneOf(VisibleWeekday.class);

    // 사용자 생성 숙제인 경우 해당 회원, 관리자 생성인 경우 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 사용자 생성 숙제의 초기화 주기 (null이면 visibleWeekdays 기반 초기화)
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CustomTodoFrequencyEnum frequency;
}
