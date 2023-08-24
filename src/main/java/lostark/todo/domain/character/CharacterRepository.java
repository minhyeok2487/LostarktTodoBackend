package lostark.todo.domain.character;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    Optional<Character> findByCharacterName(String characterName);

    @Query(value = "select DISTINCT c from Character c JOIN FETCH c.member " +
            "WHERE c.characterName = :characterName AND c.member.username = :username")
    Optional<Character> findCharacterWithMember(@Param("characterName") String characterName, @Param("username") String username);

    @Modifying
    @Query(value = "update Character c set c.itemLevel = :itemLevel where c.characterName = :characterName")
    int updateCharacterInfo(@Param("characterName") String characterName, @Param("itemLevel") Double itemLevel);

    @Query(value = "select DISTINCT c from Character c JOIN FETCH c.member")
    List<Character> findAll();
}
