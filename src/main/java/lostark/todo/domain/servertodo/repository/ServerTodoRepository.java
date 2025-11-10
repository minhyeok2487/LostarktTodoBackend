package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTodoRepository extends JpaRepository<ServerTodo, Long>, ServerTodoRepositoryCustom {
}
