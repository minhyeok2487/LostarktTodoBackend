package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodoState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTodoStateRepository extends JpaRepository<ServerTodoState, Long>, ServerTodoStateRepositoryCustom {

    // ServerTodo 삭제 시 연관된 State 삭제
    void deleteByServerTodoId(Long serverTodoId);
}
