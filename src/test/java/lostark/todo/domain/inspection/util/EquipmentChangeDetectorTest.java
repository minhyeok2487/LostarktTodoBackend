// package lostark.todo.domain.inspection.util;
// 
// import lostark.todo.domain.inspection.entity.EquipmentHistory;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// 
// import java.util.Collections;
// import java.util.List;
// 
// import static org.assertj.core.api.Assertions.assertThat;
// 
// @DisplayName("EquipmentChangeDetector 단위 테스트")
// class EquipmentChangeDetectorTest {
// 
//     private static final String CHARACTER_NAME = "테스트캐릭터";
// 
//     private static EquipmentHistory buildEquipment(String type, String name,
//                                                     Integer refinement, Integer advancedRefinement,
//                                                     Integer quality) {
//         EquipmentHistory eq = new EquipmentHistory();
//         eq.setType(type);
//         eq.setName(name);
//         eq.setRefinement(refinement);
//         eq.setAdvancedRefinement(advancedRefinement);
//         eq.setQuality(quality);
//         return eq;
//     }
// 
//     private static EquipmentHistory buildEquipmentFull(String type, String name,
//                                                         Integer refinement, Integer advancedRefinement,
//                                                         Integer quality, String grindingEffect,
//                                                         String arkPassiveEffect, String braceletEffect) {
//         EquipmentHistory eq = buildEquipment(type, name, refinement, advancedRefinement, quality);
//         eq.setGrindingEffect(grindingEffect);
//         eq.setArkPassiveEffect(arkPassiveEffect);
//         eq.setBraceletEffect(braceletEffect);
//         return eq;
//     }
// 
//     @Nested
//     @DisplayName("장비 교체 감지")
//     class EquipmentReplacement {
// 
//         @Test
//         @DisplayName("같은 슬롯에 다른 이름의 장비가 있으면 교체 알림을 생성한다")
//         void detectReplacement() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+24 운명의 파괴 롱 스태프", 24, null, 90)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, null, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("[테스트캐릭터]").contains("무기").contains("교체");
//         }
// 
//         @Test
//         @DisplayName("장비 교체 시 재련/품질 변화는 별도 알림을 생성하지 않는다")
//         void replacement_skipsOtherChanges() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("목걸이", "구 목걸이", null, null, 80)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("목걸이", "신 목걸이", null, null, 95)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("교체");
//         }
//     }
// 
//     @Nested
//     @DisplayName("재련 단계 변화 감지")
//     class RefinementChange {
// 
//         @Test
//         @DisplayName("재련 단계가 변경되면 강화 알림을 생성한다")
//         void detectRefinementChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 24, null, 97)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 25, null, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 무기가 강화되었습니다! (+24 → +25)");
//         }
// 
//         @Test
//         @DisplayName("재련 단계가 동일하면 알림을 생성하지 않는다")
//         void noChange_whenSameRefinement() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, null, 97)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, null, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).isEmpty();
//         }
//     }
// 
//     @Nested
//     @DisplayName("상급 재련 변화 감지")
//     class AdvancedRefinementChange {
// 
//         @Test
//         @DisplayName("상급 재련이 올라가면 알림을 생성한다")
//         void detectAdvancedRefinementIncrease() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 30, 80)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 40, 80)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 상의 상급 재련이 올랐습니다! (30단계 → 40단계)");
//         }
// 
//         @Test
//         @DisplayName("상급 재련이 null에서 값으로 변하면 알림을 생성한다")
//         void detectAdvancedRefinement_fromNullToValue() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, null, 97)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 무기 상급 재련이 올랐습니다! (0단계 → 1단계)");
//         }
//     }
// 
//     @Nested
//     @DisplayName("품질 변화 감지")
//     class QualityChange {
// 
//         @Test
//         @DisplayName("품질이 변경되면 알림을 생성한다")
//         void detectQualityChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("장갑", "+25 운명의 업화 배틀 글러브", 25, 2, 91)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("장갑", "+25 운명의 업화 배틀 글러브", 25, 2, 98)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 장갑 품질이 변경되었습니다! (91 → 98)");
//         }
//     }
// 
//     @Nested
//     @DisplayName("복합 변화 감지")
//     class MultipleChanges {
// 
//         @Test
//         @DisplayName("여러 슬롯에서 동시에 변화를 감지한다")
//         void detectMultipleSlotChanges() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 24, null, 97),
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 30, 80),
//                     buildEquipment("목걸이", "구 목걸이", null, null, 85)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 25, null, 97),
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 40, 80),
//                     buildEquipment("목걸이", "신 목걸이", null, null, 95)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(3);
//             assertThat(changes.get(0)).contains("무기").contains("강화");
//             assertThat(changes.get(1)).contains("상의").contains("상급 재련");
//             assertThat(changes.get(2)).contains("목걸이").contains("교체");
//         }
// 
//         @Test
//         @DisplayName("같은 장비에서 재련과 품질이 동시에 변하면 둘 다 알림을 생성한다")
//         void detectMultipleChangesOnSameSlot() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 24, null, 90)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 25, 1, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(3);
//             assertThat(changes).anyMatch(c -> c.contains("강화"));
//             assertThat(changes).anyMatch(c -> c.contains("상급 재련"));
//             assertThat(changes).anyMatch(c -> c.contains("품질"));
//         }
// 
//         @Test
//         @DisplayName("최대 알림 수를 초과하지 않는다")
//         void respectsMaxNotifications() {
//             // given: 6개 슬롯 모두 교체
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "구 무기", 20, null, 80),
//                     buildEquipment("투구", "구 투구", 20, null, 80),
//                     buildEquipment("상의", "구 상의", 20, null, 80),
//                     buildEquipment("하의", "구 하의", 20, null, 80),
//                     buildEquipment("장갑", "구 장갑", 20, null, 80),
//                     buildEquipment("어깨", "구 어깨", 20, null, 80)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "신 무기", 25, null, 97),
//                     buildEquipment("투구", "신 투구", 25, null, 97),
//                     buildEquipment("상의", "신 상의", 25, null, 97),
//                     buildEquipment("하의", "신 하의", 25, null, 97),
//                     buildEquipment("장갑", "신 장갑", 25, null, 97),
//                     buildEquipment("어깨", "신 어깨", 25, null, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(5);
//         }
//     }
// 
//     @Nested
//     @DisplayName("비전투 장비 제외")
//     class NonCombatEquipmentExclusion {
// 
//         @Test
//         @DisplayName("나침반/부적/보주 타입 장비는 변화가 있어도 알림을 생성하지 않는다")
//         void excludeNonCombatEquipment() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("나침반", "구 나침반", null, null, null),
//                     buildEquipment("부적", "구 부적", null, null, null),
//                     buildEquipment("보주", "구 보주", null, null, null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("나침반", "신 나침반", null, null, null),
//                     buildEquipment("부적", "신 부적", null, null, null),
//                     buildEquipment("보주", "신 보주", null, null, null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("비전투 장비 제외 시 전투 장비 변화는 정상 감지한다")
//         void excludeNonCombat_detectsCombatChanges() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("나침반", "구 나침반", null, null, null),
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 24, null, 97)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("나침반", "신 나침반", null, null, null),
//                     buildEquipment("무기", "+24 운명의 업화 롱 스태프", 25, null, 97)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("무기").contains("강화");
//         }
//     }
// 
//     @Nested
//     @DisplayName("연마 효과 변화 감지")
//     class GrindingEffectChange {
// 
//         @Test
//         @DisplayName("같은 악세서리에서 연마 효과가 변경되면 알림을 생성한다")
//         void detectGrindingEffectChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("목걸이", "업화 목걸이", null, null, 90,
//                             "공격력 +1.20%", "도약 +3", null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("목걸이", "업화 목걸이", null, null, 90,
//                             "공격력 +2.40%", "도약 +3", null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 목걸이 연마 효과가 변경되었습니다!");
//         }
// 
//         @Test
//         @DisplayName("연마 효과가 동일하면 알림을 생성하지 않는다")
//         void noChange_whenSameGrindingEffect() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("귀걸이", "업화 귀걸이", null, null, 85,
//                             "치명타 피해 +4.00%", null, null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("귀걸이", "업화 귀걸이", null, null, 85,
//                             "치명타 피해 +4.00%", null, null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("장비 교체 시 연마 효과 변화는 별도 알림을 생성하지 않는다")
//         void replacement_skipsGrindingEffectChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("목걸이", "구 목걸이", null, null, 80,
//                             "공격력 +1.20%", null, null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("목걸이", "신 목걸이", null, null, 95,
//                             "공격력 +2.40%", null, null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("교체");
//         }
//     }
// 
//     @Nested
//     @DisplayName("아크 패시브 포인트 변화 감지")
//     class ArkPassiveEffectChange {
// 
//         @Test
//         @DisplayName("같은 장비에서 아크 패시브가 변경되면 이전/이후 값을 포함한 알림을 생성한다")
//         void detectArkPassiveChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("귀걸이", "업화 귀걸이", null, null, 90,
//                             "치명타 피해 +4.00%", "깨달음 +12", null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("귀걸이", "업화 귀걸이", null, null, 90,
//                             "치명타 피해 +4.00%", "깨달음 +13", null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo(
//                     "[테스트캐릭터] 귀걸이 아크 패시브가 변경되었습니다! (깨달음 +12 → 깨달음 +13)");
//         }
// 
//         @Test
//         @DisplayName("아크 패시브가 null에서 값으로 변하면 알림을 생성한다")
//         void detectArkPassive_fromNullToValue() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("반지", "업화 반지", null, null, 85,
//                             null, null, null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("반지", "업화 반지", null, null, 85,
//                             null, "도약 +3", null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("아크 패시브").contains("없음 → 도약 +3");
//         }
// 
//         @Test
//         @DisplayName("장비 교체 시 아크 패시브 변화는 별도 알림을 생성하지 않는다")
//         void replacement_skipsArkPassiveChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("귀걸이", "구 귀걸이", null, null, 80,
//                             null, "깨달음 +10", null)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("귀걸이", "신 귀걸이", null, null, 95,
//                             null, "깨달음 +15", null)
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("교체");
//         }
//     }
// 
//     @Nested
//     @DisplayName("팔찌 효과 변화 감지")
//     class BraceletEffectChange {
// 
//         @Test
//         @DisplayName("같은 팔찌에서 효과가 변경되면 알림을 생성한다")
//         void detectBraceletEffectChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("팔찌", "화평석 팔찌", null, null, null,
//                             null, null, "치명 +80 신속 +80 [상급] 정밀")
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("팔찌", "화평석 팔찌", null, null, null,
//                             null, null, "치명 +80 신속 +80 [상급] 정밀 [상급] 순환")
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).isEqualTo("[테스트캐릭터] 팔찌 효과가 변경되었습니다!");
//         }
// 
//         @Test
//         @DisplayName("팔찌 효과가 동일하면 알림을 생성하지 않는다")
//         void noChange_whenSameBraceletEffect() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("팔찌", "화평석 팔찌", null, null, null,
//                             null, null, "치명 +80 신속 +80")
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("팔찌", "화평석 팔찌", null, null, null,
//                             null, null, "치명 +80 신속 +80")
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("팔찌 교체 시 효과 변화는 별도 알림을 생성하지 않는다")
//         void replacement_skipsBraceletEffectChange() {
//             // given
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipmentFull("팔찌", "구 팔찌", null, null, null,
//                             null, null, "치명 +60")
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipmentFull("팔찌", "신 팔찌", null, null, null,
//                             null, null, "치명 +80 신속 +80")
//             );
// 
//             // when
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             // then
//             assertThat(changes).hasSize(1);
//             assertThat(changes.get(0)).contains("교체");
//         }
//     }
// 
//     @Nested
//     @DisplayName("엣지 케이스")
//     class EdgeCases {
// 
//         @Test
//         @DisplayName("이전 장비가 null이면 빈 결과를 반환한다")
//         void nullPreviousEquipments_returnsEmpty() {
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97)
//             );
// 
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, null, next);
// 
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("이전 장비가 비어있으면 빈 결과를 반환한다")
//         void emptyPreviousEquipments_returnsEmpty() {
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97)
//             );
// 
//             List<String> changes = EquipmentChangeDetector.detectChanges(
//                     CHARACTER_NAME, Collections.emptyList(), next);
// 
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("새 장비가 null이면 빈 결과를 반환한다")
//         void nullNewEquipments_returnsEmpty() {
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97)
//             );
// 
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, null);
// 
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("새 장비에 이전에 없던 슬롯이 있으면 무시한다")
//         void newSlotWithoutPrevious_ignored() {
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 1, 97),
//                     buildEquipment("팔찌", "화평석 팔찌", null, null, null)
//             );
// 
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             assertThat(changes).isEmpty();
//         }
// 
//         @Test
//         @DisplayName("모든 장비가 동일하면 빈 결과를 반환한다")
//         void noChanges_returnsEmpty() {
//             List<EquipmentHistory> prev = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 40, 100),
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 40, 100)
//             );
//             List<EquipmentHistory> next = List.of(
//                     buildEquipment("무기", "+25 운명의 업화 롱 스태프", 25, 40, 100),
//                     buildEquipment("상의", "+25 운명의 업화 배틀 아머", 25, 40, 100)
//             );
// 
//             List<String> changes = EquipmentChangeDetector.detectChanges(CHARACTER_NAME, prev, next);
// 
//             assertThat(changes).isEmpty();
//         }
//     }
// }
