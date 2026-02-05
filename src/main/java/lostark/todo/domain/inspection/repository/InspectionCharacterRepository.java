package lostark.todo.domain.inspection.repository;

import lostark.todo.domain.inspection.entity.InspectionCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionCharacterRepository extends JpaRepository<InspectionCharacter, Long>,
        InspectionCharacterCustomRepository {
}
