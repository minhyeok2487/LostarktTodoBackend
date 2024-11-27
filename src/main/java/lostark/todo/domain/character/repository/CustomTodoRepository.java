package lostark.todo.domain.character.repository;

import lostark.todo.domain.character.entity.CustomTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTodoRepository extends JpaRepository<CustomTodo, Long>, CustomTodoCustomRepository {

}
