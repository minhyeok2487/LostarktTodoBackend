package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralTodoCategoryRepository extends JpaRepository<GeneralTodoCategory, Long>, GeneralTodoCategoryRepositoryCustom {
}
