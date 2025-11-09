package lostark.todo.domain.servertodo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.servertodo.entity.ServerTodo;

import java.util.List;

import static lostark.todo.domain.servertodo.entity.QServerTodo.serverTodo;

@RequiredArgsConstructor
public class ServerTodoRepositoryImpl implements ServerTodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
