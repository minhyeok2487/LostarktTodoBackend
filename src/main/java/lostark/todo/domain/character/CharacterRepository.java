package lostark.todo.domain.character;

import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    @Query(value = "SELECT DISTINCT c FROM Character c LEFT JOIN FETCH c.todoList WHERE c.member = :member ORDER BY c.sortNumber ASC")
    List<Character> findByMember(@Param("member") Member member);

    Optional<Character> findByCharacterName(String characterName);

    @Query(value = "select DISTINCT c from Character c JOIN FETCH c.member " +
            "WHERE c.characterName = :characterName AND c.member.username = :username")
    Optional<Character> findCharacterWithMember(@Param("characterName") String characterName, @Param("username") String username);

    @Query(value = "select DISTINCT c from Character c JOIN FETCH c.member " +
            "WHERE c.id = :characterId AND c.member.username = :username")
    Optional<Character> findCharacterWithMember(@Param("characterId") long characterId, @Param("username") String username);

    void deleteByMember(Member member);

    int countByMemberAndServerNameAndGoldCharacterIsTrue(Member member, String serverName);

    @Query(value = "SELECT c.serverName, COUNT(c.id) FROM Character c WHERE c.member = :member GROUP BY c.serverName")
    List<Object[]> findCountGroupByServerName(@Param("member") Member member);

    @Query(value = "SELECT c FROM Character c WHERE c.member = :member AND c.serverName = :serverName")
    List<Character> findCharacterListServerName(@Param("member") Member member, @Param("serverName") String serverName);

    @Modifying
    @Query(value = "UPDATE Character c SET " +
            "c.challengeAbyss = false, c.challengeGuardian = false, " +
            "c.weekTodo.weekEpona = 0, c.weekTodo.silmaelChange = false")
    int updateWeekContent();

    @Modifying
    @Query(value = "UPDATE Character c SET " +
            "c.dayTodo.eponaGauge = CASE WHEN (c.dayTodo.eponaGauge + (3 - c.dayTodo.eponaCheck2) * 10) > 100 THEN 100 ELSE (c.dayTodo.eponaGauge + (3 - c.dayTodo.eponaCheck2) * 10) END, " +
            "c.dayTodo.chaosGauge = CASE WHEN (c.dayTodo.chaosGauge + (2 - c.dayTodo.chaosCheck) * 10) > 100 THEN 100 ELSE (c.dayTodo.chaosGauge + (2 - c.dayTodo.chaosCheck) * 10) END," +
            "c.dayTodo.guardianGauge = CASE WHEN (c.dayTodo.guardianGauge + (1 - c.dayTodo.guardianCheck) * 10) > 100 THEN 100 ELSE (c.dayTodo.guardianGauge + (1 - c.dayTodo.guardianCheck) * 10) END")
    int updateDayContentGauge();

    @Modifying
    @Query(value = "UPDATE Character c SET " +
            "c.dayTodo.eponaCheck2 = 0, c.dayTodo.chaosCheck = 0, c.dayTodo.guardianCheck = 0")
    int updateDayContentCheck();

    @Modifying
    @Query(value = "UPDATE Character c SET c.dayTodo.chaosGold = " +
            "CASE WHEN c.dayTodo.chaosGauge >= 40 THEN (:price * 4.0) " +
            "WHEN c.dayTodo.chaosGauge >=20 AND c.dayTodo.chaosGauge < 40 THEN (:price * 3.0) " +
            "ELSE (:price *2.0) END " +
            "WHERE c.dayTodo.chaos = :dayContent")
    void updateDayContentPriceChaos(@Param("dayContent") DayContent dayContent, @Param("price") Double price);

    @Modifying
    @Query(value = "UPDATE Character c SET c.dayTodo.guardianGold = " +
            "CASE WHEN c.dayTodo.guardianGauge >= 20 THEN (:price * 2.0) " +
            "ELSE :price END " +
            "WHERE c.dayTodo.guardian = :dayContent")
    void updateDayContentPriceGuardian(@Param("dayContent") DayContent dayContent, @Param("price") Double price);

    @Modifying
    @Query(value = "UPDATE Character c SET c.challengeAbyss = true , c.challengeGuardian = true , c.weekTodo.weekEpona = 2, c.weekTodo.silmaelChange = true")
    int beforeUpdate();



}
