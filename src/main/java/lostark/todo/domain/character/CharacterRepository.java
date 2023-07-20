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

    @Modifying
    @Query(value = "update Character c set c.itemLevel = :itemLevel where c.characterName = :characterName")
    int updateCharacterInfo(@Param("characterName") String characterName, @Param("itemLevel") Double itemLevel);

}
