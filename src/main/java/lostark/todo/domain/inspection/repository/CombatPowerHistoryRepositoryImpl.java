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

        // 전투력이 다른 가장 최근 기록의 날짜를 찾아서, 그 이후 레코드 수를 카운트
        Long count = factory.select(combatPowerHistory.count())
                .from(combatPowerHistory)
                .where(
                        combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
                        combatPowerHistory.combatPower.eq(latestCombatPower),
                        combatPowerHistory.recordDate.goe(
                                factory.select(combatPowerHistory.recordDate.max())
                                        .from(combatPowerHistory)
                                        .where(
                                                combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
                                                combatPowerHistory.combatPower.ne(latestCombatPower)
                                        )
                        )
                )
                .fetchOne();

        // 전투력이 다른 기록이 전혀 없는 경우 전체 개수 반환
        if (count == null || count == 0) {
            Long totalCount = factory.select(combatPowerHistory.count())
                    .from(combatPowerHistory)
                    .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
                    .fetchOne();
            return totalCount != null ? totalCount : 0;
        }

        return count;
    }
}
