package lostark.todo.domain.servertodo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "server_todo_state")
public class ServerTodoState extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "server_todo_state_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "server_todo_id")
    private ServerTodo serverTodo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 50)
    private String serverName;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean enabled;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean checked;

    public static ServerTodoState create(ServerTodo serverTodo, Member member, String serverName, boolean defaultEnabled) {
        ServerTodoState state = new ServerTodoState();
        state.serverTodo = serverTodo;
        state.member = member;
        state.serverName = serverName;
        state.enabled = defaultEnabled;
        state.checked = false;
        return state;
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
