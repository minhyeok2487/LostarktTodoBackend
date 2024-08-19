package lostark.todo.domain.customTodo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTodoRepository extends JpaRepository<CustomTodo, Long>, CustomTodoCustomRepository {

}
