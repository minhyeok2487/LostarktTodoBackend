package lostark.todo.domain.character.repository;

import lostark.todo.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CharacterRepository extends JpaRepository<Character, Long>, CharacterCustomRepository {

    @Modifying
    @Query(value = "UPDATE Character c SET " +
            "c.challengeAbyss = false, c.challengeGuardian = false, " +
            "c.weekTodo.weekEpona = 0, c.weekTodo.silmaelChange = false, c.weekTodo.elysianCount = 0")
    int updateWeekContent();

    @Modifying
    @Query(value = "UPDATE Character c SET c.dayTodo.weekTotalGold = 0 WHERE c.dayTodo.weekTotalGold > 0")
    void updateWeekDayTodoTotalGold();
}
