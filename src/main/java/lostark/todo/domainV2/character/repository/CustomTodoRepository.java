package lostark.todo.domainV2.character.repository;

import lostark.todo.domainV2.character.entity.CustomTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTodoRepository extends JpaRepository<CustomTodo, Long>, CustomTodoCustomRepository {

}
