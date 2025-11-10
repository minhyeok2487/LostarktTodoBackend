package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodo;

import java.util.List;

public interface ServerTodoRepositoryCustom {

    List<ServerTodo> findAllVisible();
}
