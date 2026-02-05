package lostark.todo.domain.inspection.repository;

import lostark.todo.domain.inspection.entity.CombatPowerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatPowerHistoryRepository extends JpaRepository<CombatPowerHistory, Long>,
        CombatPowerHistoryCustomRepository {
}
