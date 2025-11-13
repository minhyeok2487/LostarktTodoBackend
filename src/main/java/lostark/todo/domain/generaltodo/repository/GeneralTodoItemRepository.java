package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralTodoItemRepository extends JpaRepository<GeneralTodoItem, Long>, GeneralTodoItemRepositoryCustom {

    boolean existsByStatusId(Long statusId);
}
