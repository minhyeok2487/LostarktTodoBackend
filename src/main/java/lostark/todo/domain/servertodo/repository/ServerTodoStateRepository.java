package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodoState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTodoStateRepository extends JpaRepository<ServerTodoState, Long>, ServerTodoStateRepositoryCustom {
}
