package lostark.todo.domain.inspection.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.inspection.entity.CombatPowerHistory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.domain.inspection.entity.QCombatPowerHistory.combatPowerHistory;

@RequiredArgsConstructor
public class CombatPowerHistoryRepositoryImpl implements CombatPowerHistoryCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<CombatPowerHistory> findByCharacterAndDateRange(long inspectionCharacterId,
                                                                 LocalDate startDate, LocalDate endDate) {
        return factory.selectFrom(combatPowerHistory).distinct()
                .leftJoin(combatPowerHistory.arkgridEffects).fetchJoin()
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

    @Override
    public List<CombatPowerHistory> findLatest2(long inspectionCharacterId) {
        return factory.selectFrom(combatPowerHistory)
                .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
                .orderBy(combatPowerHistory.recordDate.desc())
                .limit(2)
                .fetch();
    }

    @Override
    public Map<Long, List<CombatPowerHistory>> findLatest2ByCharacterIds(List<Long> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 각 캐릭터의 최근 2개 히스토리를 한 번에 조회하기 위해
        // 전체 결과를 가져온 후 그룹핑 (캐릭터별 최근 2일치만 필요하므로 날짜 범위 제한)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<CombatPowerHistory> allHistories = factory.selectFrom(combatPowerHistory)
                .where(
                        combatPowerHistory.inspectionCharacter.id.in(characterIds),
                        combatPowerHistory.recordDate.between(startDate, endDate)
                )
                .orderBy(
                        combatPowerHistory.inspectionCharacter.id.asc(),
                        combatPowerHistory.recordDate.desc()
                )
                .fetch();

        // 캐릭터별로 그룹핑 후 각각 최근 2개만 유지
        return allHistories.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getInspectionCharacter().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream().limit(2).collect(Collectors.toList())
                        )
                ));
    }

    @Override
    public Map<Long, Long> countConsecutiveUnchangedDaysBatch(List<Long> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> result = new HashMap<>();
        for (Long characterId : characterIds) {
            result.put(characterId, countConsecutiveUnchangedDays(characterId));
        }
        return result;
    }
}
