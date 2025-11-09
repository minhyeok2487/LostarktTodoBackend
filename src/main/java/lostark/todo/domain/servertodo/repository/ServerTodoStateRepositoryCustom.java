package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodoState;

import java.util.List;

public interface ServerTodoStateRepositoryCustom {

    List<ServerTodoState> findByMemberAndServerNames(Long memberId, List<String> serverNames);

    ServerTodoState findByMemberAndTodo(Long memberId, Long todoId, String serverName);
}
