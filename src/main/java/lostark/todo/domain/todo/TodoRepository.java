package lostark.todo.domain.todo;

import lostark.todo.domain.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Todo findByCharacterAndContentName(Character character, TodoContentName contentName);
}
