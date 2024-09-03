package lostark.todo.domain.todoV2;

import lostark.todo.domain.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TodoV2Repository extends JpaRepository<TodoV2, Long> {

    @Query("SELECT t FROM TodoV2 t WHERE t.character = :character AND t.weekContent.weekCategory =:weekCategory AND t.weekContent.gate = :gate")
    Optional<TodoV2> findByCharacterAndWeekCategoryAndGate(Character character, String weekCategory, int gate);

    @Query("SELECT t FROM TodoV2 t WHERE t.character = :character AND t.weekContent.weekCategory =:weekCategory AND t.coolTime >= 1")
    List<TodoV2> findAllCharacterAndWeekCategory(Character character, String weekCategory);

    @Modifying
    @Query("UPDATE TodoV2 t " +
            "SET t.coolTime = " +
            "CASE " +
            "  WHEN t.coolTime = 2 AND t.isChecked = true THEN 0 " +
            "  WHEN t.coolTime = 2 AND t.isChecked = false THEN 1 " +
            "  ELSE 2 " +
            "END " +
            "WHERE t.weekContent IN (SELECT wc FROM WeekContent wc WHERE wc.coolTime = 2)")
    int resetTodoV2CoolTime2();

    @Modifying
    @Query("UPDATE TodoV2 t SET t.isChecked = false")
    int resetTodoV2();

    @Query("SELECT t FROM TodoV2 t WHERE t.character = :character AND t.weekContent.weekCategory = :weekCategory")
    List<TodoV2> findByCharacterAndWeekCategory(Character character, String weekCategory);
}
