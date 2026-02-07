package lostark.todo.domain.inspection.repository;

import lostark.todo.domain.inspection.entity.CombatPowerHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CombatPowerHistoryCustomRepository {

    List<CombatPowerHistory> findByCharacterAndDateRange(long inspectionCharacterId,
                                                         LocalDate startDate, LocalDate endDate);

    Optional<CombatPowerHistory> findLatest(long inspectionCharacterId);

    List<CombatPowerHistory> findLatest2(long inspectionCharacterId);

    Optional<CombatPowerHistory> findByCharacterAndDate(long inspectionCharacterId, LocalDate date);

    long countConsecutiveUnchangedDays(long inspectionCharacterId);

    Map<Long, List<CombatPowerHistory>> findLatest2ByCharacterIds(List<Long> characterIds);

    Map<Long, Long> countConsecutiveUnchangedDaysBatch(List<Long> characterIds);
}
