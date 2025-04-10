package lostark.todo.domain.character.repository;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.RaidBusGold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RaidBusGoldRepository extends JpaRepository<RaidBusGold, Long> {

    Optional<RaidBusGold> findByCharacterAndWeekCategory(Character character, String weekCategory);

    @Modifying
    @Query("delete from RaidBusGold r where r.fixed = false")
    void deleteAllRaidBusGold();
}
