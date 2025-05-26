package lostark.todo.domain.member.repository;

import lostark.todo.domain.member.entity.LifeEnergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LifeEnergyRepository extends JpaRepository<LifeEnergy, Long> {

    @Modifying
    @Query("UPDATE LifeEnergy le SET le.energy = " +
            "LEAST(le.maxEnergy, le.energy + (CASE WHEN le.beatrice = true THEN 99 ELSE 90 END))")
    int addEnergyToAllLifeEnergies();
}
