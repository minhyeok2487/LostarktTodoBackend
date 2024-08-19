package lostark.todo.domain.customTodo;

import java.util.List;

public interface CustomTodoCustomRepository {

    List<CustomTodo> search(String username);
}
