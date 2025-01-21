package lostark.todo.domain.character.repository;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.RaidBusGold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaidBusGoldRepository extends JpaRepository<RaidBusGold, Long> {

    Optional<RaidBusGold> findByCharacterAndWeekCategory(Character character, String weekCategory);
}
