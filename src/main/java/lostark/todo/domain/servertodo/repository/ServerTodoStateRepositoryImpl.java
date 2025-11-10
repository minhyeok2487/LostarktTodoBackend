package lostark.todo.domain.servertodo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.servertodo.entity.ServerTodoState;

import java.util.List;

import static lostark.todo.domain.servertodo.entity.QServerTodoState.serverTodoState;

@RequiredArgsConstructor
public class ServerTodoStateRepositoryImpl implements ServerTodoStateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ServerTodoState> findByMemberAndServerNames(Long memberId, List<String> serverNames) {
        return queryFactory.selectFrom(serverTodoState)
                .where(
                        memberIdEq(memberId),
                        serverNameIn(serverNames)
                )
                .fetch();
    }

    @Override
    public ServerTodoState findByMemberAndTodo(Long memberId, Long todoId, String serverName) {
        return queryFactory.selectFrom(serverTodoState)
                .where(
                        memberIdEq(memberId),
                        serverTodoState.serverTodo.id.eq(todoId),
                        serverNameEq(serverName)
                )
                .fetchOne();
    }

    @Override
    public long resetAllChecked() {
        return queryFactory.update(serverTodoState)
                .set(serverTodoState.checked, false)
                .execute();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return serverTodoState.member.id.eq(memberId);
    }

    private BooleanExpression serverNameEq(String serverName) {
        if (serverName == null) {
            return null;
        }
        return serverTodoState.serverName.eq(serverName);
    }

    private BooleanExpression serverNameIn(List<String> serverNames) {
        if (serverNames == null || serverNames.isEmpty()) {
            return null;
        }
        return serverTodoState.serverName.in(serverNames);
    }
}
