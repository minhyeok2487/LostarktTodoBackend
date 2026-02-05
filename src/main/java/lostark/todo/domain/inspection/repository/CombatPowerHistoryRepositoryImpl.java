package lostark.todo.domain.inspection.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.inspection.entity.CombatPowerHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.inspection.entity.QCombatPowerHistory.combatPowerHistory;

@RequiredArgsConstructor
public class CombatPowerHistoryRepositoryImpl implements CombatPowerHistoryCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<CombatPowerHistory> findByCharacterAndDateRange(long inspectionCharacterId,
                                                                 LocalDate startDate, LocalDate endDate) {
        return factory.selectFrom(combatPowerHistory)
                .where(
                        combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
                        combatPowerHistory.recordDate.between(startDate, endDate)
                )
                .orderBy(combatPowerHistory.recordDate.asc())
                .fetch();
    }

    @Override
    public Optional<CombatPowerHistory> findLatest(long inspectionCharacterId) {
        CombatPowerHistory result = factory.selectFrom(combatPowerHistory)
                .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
                .orderBy(combatPowerHistory.recordDate.desc())
                .fetchFirst();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<CombatPowerHistory> findByCharacterAndDate(long inspectionCharacterId, LocalDate date) {
        CombatPowerHistory result = factory.selectFrom(combatPowerHistory)
                .where(
                        combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
                        combatPowerHistory.recordDate.eq(date)
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public long countConsecutiveUnchangedDays(long inspectionCharacterId) {
        // 최신 기록의 전투력 가져오기
        CombatPowerHistory latest = factory.selectFrom(combatPowerHistory)
                .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
                .orderBy(combatPowerHistory.recordDate.desc())
                .fetchFirst();

        if (latest == null) {
            return 0;
        }

        double latestCombatPower = latest.getCombatPower();

        // 같은 전투력을 가진 연속 기록 수 카운트
        List<CombatPowerHistory> recentHistories = factory.selectFrom(combatPowerHistory)
                .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
                .orderBy(combatPowerHistory.recordDate.desc())
                .fetch();

        long count = 0;
        for (CombatPowerHistory history : recentHistories) {
            if (Double.compare(history.getCombatPower(), latestCombatPower) == 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
