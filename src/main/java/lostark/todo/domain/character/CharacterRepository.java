package lostark.todo.domain.character;

import lostark.todo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    @Query(value = "SELECT DISTINCT c FROM Character c LEFT JOIN FETCH c.todoList WHERE c.member = :member ORDER BY c.sortNumber ASC")
    List<Character> findByMember(@Param("member") Member member);

    Optional<Character> findByCharacterName(String characterName);

    @Query(value = "select DISTINCT c from Character c JOIN FETCH c.member " +
            "WHERE c.characterName = :characterName AND c.member.username = :username")
    Optional<Character> findCharacterWithMember(@Param("characterName") String characterName, @Param("username") String username);

    void deleteByMember(Member member);

    int countByMemberAndServerNameAndGoldCharacterIsTrue(Member member, String serverName);

    @Query(value = "SELECT c.serverName, COUNT(c.id) FROM Character c WHERE c.member = :member GROUP BY c.serverName")
    List<Object[]> findCountGroupByServerName(@Param("member") Member member);

    @Query(value = "SELECT c FROM Character c WHERE c.member = :member AND c.serverName = :serverName")
    List<Character> findCharacterListServerName(@Param("member") Member member, @Param("serverName") String serverName);
}
