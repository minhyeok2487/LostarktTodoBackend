package lostark.todo.domain.servertodo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.servertodo.entity.ServerTodo;

import java.util.List;

import static lostark.todo.domain.servertodo.entity.QServerTodo.serverTodo;

@RequiredArgsConstructor
public class ServerTodoRepositoryImpl implements ServerTodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ServerTodo> findAllVisible(Long memberId) {
        return queryFactory.selectFrom(serverTodo)
                .leftJoin(serverTodo.visibleWeekdays).fetchJoin()
                .where(
                        serverTodo.member.isNull()  // 관리자 생성
                                .or(serverTodo.member.id.eq(memberId))  // 또는 본인 생성
                )
                .distinct()
                .fetch();
    }
}
