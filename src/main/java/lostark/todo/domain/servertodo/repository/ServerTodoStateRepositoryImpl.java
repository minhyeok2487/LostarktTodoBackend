package lostark.todo.domain.servertodo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.servertodo.entity.ServerTodoState;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;

import java.util.List;

import static lostark.todo.domain.servertodo.entity.QServerTodo.serverTodo;
import static lostark.todo.domain.servertodo.entity.QServerTodoState.serverTodoState;

@RequiredArgsConstructor
public class ServerTodoStateRepositoryImpl implements ServerTodoStateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ServerTodoState> findByMemberAndServerNames(Long memberId, List<String> serverNames) {
        return queryFactory.selectFrom(serverTodoState)
                .join(serverTodoState.serverTodo).fetchJoin()
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
    public long resetByVisibleWeekday(VisibleWeekday weekday) {
        // 1. 해당 요일에 해당하는 ServerTodo ID 조회 (frequency가 null인 것만 - 관리자 생성)
        List<Long> todoIds = queryFactory
                .select(serverTodo.id)
                .from(serverTodo)
                .where(
                        serverTodo.visibleWeekdays.contains(weekday),
                        serverTodo.frequency.isNull()
                )
                .fetch();

        if (todoIds.isEmpty()) {
            return 0;
        }

        // 2. 해당 ServerTodo에 연결된 State들만 bulk UPDATE
        return queryFactory.update(serverTodoState)
                .set(serverTodoState.checked, false)
                .where(serverTodoState.serverTodo.id.in(todoIds))
                .execute();
    }

    @Override
    public long resetByFrequency(CustomTodoFrequencyEnum frequency) {
        // 사용자 생성 숙제 중 해당 frequency인 것만 bulk UPDATE
        return queryFactory.update(serverTodoState)
                .set(serverTodoState.checked, false)
                .where(serverTodoState.serverTodo.frequency.eq(frequency))
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
