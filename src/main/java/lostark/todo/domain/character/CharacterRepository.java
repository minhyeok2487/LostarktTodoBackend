package lostark.todo.domain.character;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    //Character findByCharacterName(String characterName);

    Optional<Character> findByCharacterName(String characterName);
}
