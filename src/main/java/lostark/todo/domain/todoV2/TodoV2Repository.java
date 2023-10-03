package lostark.todo.domain.todoV2;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoV2Repository extends JpaRepository<TodoV2, Long> {

    Optional<TodoV2> findByCharacterAndWeekContent(Character character, WeekContent weekContent);
}
