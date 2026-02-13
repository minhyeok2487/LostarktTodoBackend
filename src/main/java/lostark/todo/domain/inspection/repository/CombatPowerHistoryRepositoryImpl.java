// package lostark.todo.domain.inspection.repository;
// 
// import com.querydsl.jpa.impl.JPAQueryFactory;
// import lombok.RequiredArgsConstructor;
// import lostark.todo.domain.inspection.entity.CombatPowerHistory;
// 
// import java.time.LocalDate;
// import java.util.*;
// import java.util.stream.Collectors;
// 
// import static lostark.todo.domain.inspection.entity.QCombatPowerHistory.combatPowerHistory;
// 
// @RequiredArgsConstructor
// public class CombatPowerHistoryRepositoryImpl implements CombatPowerHistoryCustomRepository {
// 
//     private final JPAQueryFactory factory;
// 
//     @Override
//     public List<CombatPowerHistory> findByCharacterAndDateRange(long inspectionCharacterId,
//                                                                  LocalDate startDate, LocalDate endDate) {
//         return factory.selectFrom(combatPowerHistory)
//                 .where(
//                         combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
//                         combatPowerHistory.recordDate.between(startDate, endDate)
//                 )
//                 .orderBy(combatPowerHistory.recordDate.asc())
//                 .fetch();
//     }
// 
//     @Override
//     public Optional<CombatPowerHistory> findLatest(long inspectionCharacterId) {
//         CombatPowerHistory result = factory.selectFrom(combatPowerHistory)
//                 .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
//                 .orderBy(combatPowerHistory.recordDate.desc())
//                 .fetchFirst();
//         return Optional.ofNullable(result);
//     }
// 
//     @Override
//     public Optional<CombatPowerHistory> findByCharacterAndDate(long inspectionCharacterId, LocalDate date) {
//         CombatPowerHistory result = factory.selectFrom(combatPowerHistory)
//                 .where(
//                         combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
//                         combatPowerHistory.recordDate.eq(date)
//                 )
//                 .fetchOne();
//         return Optional.ofNullable(result);
//     }
// 
//     @Override
//     public long countConsecutiveUnchangedDays(long inspectionCharacterId) {
//         // 최근 90일 데이터를 조회 후 Java에서 연속 무변동 일수 계산
//         LocalDate cutoffDate = LocalDate.now().minusDays(90);
//         List<CombatPowerHistory> histories = factory.selectFrom(combatPowerHistory)
//                 .where(
//                         combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId),
//                         combatPowerHistory.recordDate.goe(cutoffDate)
//                 )
//                 .orderBy(combatPowerHistory.recordDate.desc())
//                 .fetch();
// 
//         if (histories.isEmpty()) {
//             return 0;
//         }
// 
//         double latestPower = histories.get(0).getCombatPower();
//         long count = 0;
//         for (CombatPowerHistory h : histories) {
//             if (Double.compare(h.getCombatPower(), latestPower) == 0) {
//                 count++;
//             } else {
//                 break;
//             }
//         }
//         return count;
//     }
// 
//     @Override
//     public List<CombatPowerHistory> findLatest2(long inspectionCharacterId) {
//         return factory.selectFrom(combatPowerHistory)
//                 .where(combatPowerHistory.inspectionCharacter.id.eq(inspectionCharacterId))
//                 .orderBy(combatPowerHistory.recordDate.desc())
//                 .limit(2)
//                 .fetch();
//     }
// 
//     @Override
//     public Map<Long, List<CombatPowerHistory>> findLatest2ByCharacterIds(List<Long> characterIds) {
//         if (characterIds == null || characterIds.isEmpty()) {
//             return Collections.emptyMap();
//         }
// 
//         // 각 캐릭터의 최근 2개 히스토리를 한 번에 조회
//         // 목록 조회용이므로 최근 3일치면 충분
//         LocalDate endDate = LocalDate.now();
//         LocalDate startDate = endDate.minusDays(3);
// 
//         List<CombatPowerHistory> allHistories = factory.selectFrom(combatPowerHistory)
//                 .where(
//                         combatPowerHistory.inspectionCharacter.id.in(characterIds),
//                         combatPowerHistory.recordDate.between(startDate, endDate)
//                 )
//                 .orderBy(
//                         combatPowerHistory.inspectionCharacter.id.asc(),
//                         combatPowerHistory.recordDate.desc()
//                 )
//                 .fetch();
// 
//         // 캐릭터별로 그룹핑 후 각각 최근 2개만 유지
//         return allHistories.stream()
//                 .collect(Collectors.groupingBy(
//                         h -> h.getInspectionCharacter().getId(),
//                         Collectors.collectingAndThen(
//                                 Collectors.toList(),
//                                 list -> list.stream().limit(2).collect(Collectors.toList())
//                         )
//                 ));
//     }
// 
//     @Override
//     public Map<Long, Long> countConsecutiveUnchangedDaysBatch(List<Long> characterIds) {
//         if (characterIds == null || characterIds.isEmpty()) {
//             return Collections.emptyMap();
//         }
// 
//         // 모든 캐릭터의 히스토리를 한 번의 쿼리로 조회 (최근 90일)
//         LocalDate cutoffDate = LocalDate.now().minusDays(90);
//         List<CombatPowerHistory> allHistories = factory.selectFrom(combatPowerHistory)
//                 .where(
//                         combatPowerHistory.inspectionCharacter.id.in(characterIds),
//                         combatPowerHistory.recordDate.goe(cutoffDate)
//                 )
//                 .orderBy(
//                         combatPowerHistory.inspectionCharacter.id.asc(),
//                         combatPowerHistory.recordDate.desc()
//                 )
//                 .fetch();
// 
//         // 캐릭터별 그룹핑
//         Map<Long, List<CombatPowerHistory>> grouped = allHistories.stream()
//                 .collect(Collectors.groupingBy(h -> h.getInspectionCharacter().getId()));
// 
//         // 각 캐릭터별 연속 무변동 일수 계산 (Java에서 처리)
//         Map<Long, Long> result = new HashMap<>();
//         for (Long charId : characterIds) {
//             List<CombatPowerHistory> histories = grouped.get(charId);
//             if (histories == null || histories.isEmpty()) {
//                 result.put(charId, 0L);
//                 continue;
//             }
//             // histories는 날짜 내림차순 정렬됨
//             double latestPower = histories.get(0).getCombatPower();
//             long count = 0;
//             for (CombatPowerHistory h : histories) {
//                 if (Double.compare(h.getCombatPower(), latestPower) == 0) {
//                     count++;
//                 } else {
//                     break;
//                 }
//             }
//             result.put(charId, count);
//         }
//         return result;
//     }
// }
