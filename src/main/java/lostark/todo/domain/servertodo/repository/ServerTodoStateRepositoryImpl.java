package lostark.todo.domain.servertodo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerTodoStateRepositoryImpl implements ServerTodoStateRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
