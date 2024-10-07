package lostark.todo.domainV2.character.repository;

import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long>, CharacterCustomRepository {

    int countByMemberAndServerNameAndGoldCharacterIsTrue(Member member, String serverName);

    @Query(value = "SELECT c FROM Character c WHERE c.member = :member AND c.serverName = :serverName")
    List<Character> findCharacterListServerName(@Param("member") Member member, @Param("serverName") String serverName);

    @Modifying
    @Query(value = "UPDATE Character c SET " +
            "c.challengeAbyss = false, c.challengeGuardian = false, " +
            "c.weekTodo.weekEpona = 0, c.weekTodo.silmaelChange = false")
    int updateWeekContent();

    List<Character> findAllByCharacterName(String characterName);

    @Query("SELECT DISTINCT c FROM Character c " +
            "LEFT JOIN FETCH c.todoV2List t " +
            "LEFT JOIN FETCH t.weekContent " +
            "LEFT JOIN FETCH c.dayTodo.guardian " +
            "LEFT JOIN FETCH c.dayTodo.chaos " +
            "WHERE c.member.username = :username")
    List<Character> findAllByUsername(String username);

    @Modifying
    @Query(value = "UPDATE Character c SET c.dayTodo.weekTotalGold = 0 WHERE c.dayTodo.weekTotalGold > 0")
    void updateWeekDayTodoTotalGold();
}
