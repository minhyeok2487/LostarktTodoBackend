package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralTodoStatusRepository extends JpaRepository<GeneralTodoStatus, Long>, GeneralTodoStatusRepositoryCustom {

}
