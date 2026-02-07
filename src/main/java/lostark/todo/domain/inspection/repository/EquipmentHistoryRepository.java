package lostark.todo.domain.inspection.repository;

import lostark.todo.domain.inspection.entity.EquipmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentHistoryRepository extends JpaRepository<EquipmentHistory, Long> {
}
