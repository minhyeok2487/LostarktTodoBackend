// package lostark.todo.domain.inspection.entity;
// 
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// 
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// 
// import static org.assertj.core.api.Assertions.assertThat;
// 
// @DisplayName("CombatPowerHistory replace 메서드 단위 테스트")
// class CombatPowerHistoryReplaceTest {
// 
//     private CombatPowerHistory history;
// 
//     @BeforeEach
//     void setUp() {
//         history = CombatPowerHistory.builder()
//                 .id(1L)
//                 .recordDate(LocalDate.now())
//                 .combatPower(2200.0)
//                 .itemLevel(1620.0)
//                 .arkgridEffects(new ArrayList<>())
//                 .equipments(new ArrayList<>())
//                 .engravings(new ArrayList<>())
//                 .cards(new ArrayList<>())
//                 .cardSetEffects(new ArrayList<>())
//                 .gems(new ArrayList<>())
//                 .arkPassivePoints(new ArrayList<>())
//                 .arkPassiveEffects(new ArrayList<>())
//                 .build();
//     }
// 
//     @Nested
//     @DisplayName("replaceEquipments 메서드")
//     class ReplaceEquipmentsTest {
// 
//         @Test
//         @DisplayName("빈 리스트에 장비 추가")
//         void addToEmpty() {
//             List<EquipmentHistory> newEquipments = List.of(
//                     createEquipment("무기", "+25 롱 스태프"),
//                     createEquipment("투구", "+25 배틀 헬멧")
//             );
// 
//             history.replaceEquipments(newEquipments);
// 
//             assertThat(history.getEquipments()).hasSize(2);
//             assertThat(history.getEquipments().get(0).getType()).isEqualTo("무기");
//             assertThat(history.getEquipments().get(0).getCombatPowerHistory()).isEqualTo(history);
//             assertThat(history.getEquipments().get(1).getType()).isEqualTo("투구");
//         }
// 
//         @Test
//         @DisplayName("기존 장비를 새 장비로 교체")
//         void replaceExisting() {
//             EquipmentHistory old = createEquipment("무기", "구 무기");
//             old.setCombatPowerHistory(history);
//             history.getEquipments().add(old);
// 
//             List<EquipmentHistory> newEquipments = List.of(
//                     createEquipment("무기", "신 무기"),
//                     createEquipment("상의", "신 상의")
//             );
// 
//             history.replaceEquipments(newEquipments);
// 
//             assertThat(history.getEquipments()).hasSize(2);
//             assertThat(history.getEquipments().get(0).getName()).isEqualTo("신 무기");
//         }
// 
//         @Test
//         @DisplayName("빈 리스트로 교체하면 전부 삭제")
//         void clearAll() {
//             EquipmentHistory old = createEquipment("무기", "구 무기");
//             old.setCombatPowerHistory(history);
//             history.getEquipments().add(old);
// 
//             history.replaceEquipments(new ArrayList<>());
// 
//             assertThat(history.getEquipments()).isEmpty();
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceEngravings 메서드")
//     class ReplaceEngravingsTest {
// 
//         @Test
//         @DisplayName("각인 추가 및 부모 참조 설정")
//         void addEngravings() {
//             List<EngravingHistory> newEngravings = List.of(
//                     EngravingHistory.builder().name("원한").level(3).grade("유물").build(),
//                     EngravingHistory.builder().name("슈퍼 차지").level(3).grade("유물").build(),
//                     EngravingHistory.builder().name("방어력 감소").level(1).grade("유물").build()
//             );
// 
//             history.replaceEngravings(newEngravings);
// 
//             assertThat(history.getEngravings()).hasSize(3);
//             assertThat(history.getEngravings().get(0).getName()).isEqualTo("원한");
//             assertThat(history.getEngravings().get(0).getLevel()).isEqualTo(3);
//             assertThat(history.getEngravings().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
// 
//         @Test
//         @DisplayName("기존 각인을 새 각인으로 교체")
//         void replaceExisting() {
//             EngravingHistory old = EngravingHistory.builder().name("구 각인").level(1).build();
//             old.setCombatPowerHistory(history);
//             history.getEngravings().add(old);
// 
//             history.replaceEngravings(List.of(
//                     EngravingHistory.builder().name("신 각인").level(3).build()
//             ));
// 
//             assertThat(history.getEngravings()).hasSize(1);
//             assertThat(history.getEngravings().get(0).getName()).isEqualTo("신 각인");
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceCards 메서드")
//     class ReplaceCardsTest {
// 
//         @Test
//         @DisplayName("카드 6장 추가")
//         void addCards() {
//             List<CardHistory> newCards = List.of(
//                     CardHistory.builder().slot(0).name("카드1").awakeCount(3).awakeTotal(5).grade("전설").build(),
//                     CardHistory.builder().slot(1).name("카드2").awakeCount(5).awakeTotal(5).grade("전설").build(),
//                     CardHistory.builder().slot(2).name("카드3").awakeCount(5).awakeTotal(5).grade("전설").build(),
//                     CardHistory.builder().slot(3).name("카드4").awakeCount(5).awakeTotal(5).grade("전설").build(),
//                     CardHistory.builder().slot(4).name("카드5").awakeCount(5).awakeTotal(5).grade("전설").build(),
//                     CardHistory.builder().slot(5).name("카드6").awakeCount(5).awakeTotal(5).grade("전설").build()
//             );
// 
//             history.replaceCards(newCards);
// 
//             assertThat(history.getCards()).hasSize(6);
//             assertThat(history.getCards().get(0).getSlot()).isEqualTo(0);
//             assertThat(history.getCards().get(0).getAwakeCount()).isEqualTo(3);
//             assertThat(history.getCards().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
// 
//         @Test
//         @DisplayName("빈 리스트로 교체하면 전부 삭제")
//         void clearAll() {
//             CardHistory old = CardHistory.builder().slot(0).name("구 카드").build();
//             old.setCombatPowerHistory(history);
//             history.getCards().add(old);
// 
//             history.replaceCards(new ArrayList<>());
// 
//             assertThat(history.getCards()).isEmpty();
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceCardSetEffects 메서드")
//     class ReplaceCardSetEffectsTest {
// 
//         @Test
//         @DisplayName("카드 세트효과 추가")
//         void addCardSetEffects() {
//             List<CardSetEffectHistory> newEffects = List.of(
//                     CardSetEffectHistory.builder().name("남겨진 바람의 절벽 (6)").description("피해 +7%").build(),
//                     CardSetEffectHistory.builder().name("세상을 구하는 빛 (6)").description("암속성 피해 감소 +30%").build()
//             );
// 
//             history.replaceCardSetEffects(newEffects);
// 
//             assertThat(history.getCardSetEffects()).hasSize(2);
//             assertThat(history.getCardSetEffects().get(0).getName()).contains("바람의 절벽");
//             assertThat(history.getCardSetEffects().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceGems 메서드")
//     class ReplaceGemsTest {
// 
//         @Test
//         @DisplayName("보석 추가 및 부모 참조 설정")
//         void addGems() {
//             List<GemHistory> newGems = List.of(
//                     GemHistory.builder().skillName("잔혈 폭격").gemSlot(0).level(10).grade("유물")
//                             .description("피해 +21%").option("멸화").build(),
//                     GemHistory.builder().skillName("회전 베기").gemSlot(1).level(10).grade("유물")
//                             .description("재사용 대기시간 -18%").option("홍염").build()
//             );
// 
//             history.replaceGems(newGems);
// 
//             assertThat(history.getGems()).hasSize(2);
//             assertThat(history.getGems().get(0).getSkillName()).isEqualTo("잔혈 폭격");
//             assertThat(history.getGems().get(0).getLevel()).isEqualTo(10);
//             assertThat(history.getGems().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
// 
//         @Test
//         @DisplayName("기존 보석 교체 시 clear 후 새로 추가")
//         void replaceExisting() {
//             GemHistory old = GemHistory.builder().skillName("구 스킬").gemSlot(0).level(7).build();
//             old.setCombatPowerHistory(history);
//             history.getGems().add(old);
// 
//             history.replaceGems(List.of(
//                     GemHistory.builder().skillName("신 스킬").gemSlot(0).level(10).build()
//             ));
// 
//             assertThat(history.getGems()).hasSize(1);
//             assertThat(history.getGems().get(0).getSkillName()).isEqualTo("신 스킬");
//             assertThat(history.getGems().get(0).getLevel()).isEqualTo(10);
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceArkPassivePoints 메서드")
//     class ReplaceArkPassivePointsTest {
// 
//         @Test
//         @DisplayName("아크패시브 포인트 추가")
//         void addPoints() {
//             List<ArkPassivePointHistory> newPoints = List.of(
//                     ArkPassivePointHistory.builder().name("깨달음").value(30).description("깨달음 포인트").build(),
//                     ArkPassivePointHistory.builder().name("도약").value(25).description("도약 포인트").build(),
//                     ArkPassivePointHistory.builder().name("진화").value(20).description("진화 포인트").build()
//             );
// 
//             history.replaceArkPassivePoints(newPoints);
// 
//             assertThat(history.getArkPassivePoints()).hasSize(3);
//             assertThat(history.getArkPassivePoints().get(0).getName()).isEqualTo("깨달음");
//             assertThat(history.getArkPassivePoints().get(0).getValue()).isEqualTo(30);
//             assertThat(history.getArkPassivePoints().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
// 
//         @Test
//         @DisplayName("빈 리스트로 교체하면 전부 삭제")
//         void clearAll() {
//             ArkPassivePointHistory old = ArkPassivePointHistory.builder().name("구 포인트").value(10).build();
//             old.setCombatPowerHistory(history);
//             history.getArkPassivePoints().add(old);
// 
//             history.replaceArkPassivePoints(new ArrayList<>());
// 
//             assertThat(history.getArkPassivePoints()).isEmpty();
//         }
//     }
// 
//     @Nested
//     @DisplayName("replaceArkPassiveEffects 메서드")
//     class ReplaceArkPassiveEffectsTest {
// 
//         @Test
//         @DisplayName("아크패시브 효과 추가")
//         void addEffects() {
//             List<ArkPassiveEffectHistory> newEffects = List.of(
//                     ArkPassiveEffectHistory.builder().category("깨달음").effectName("공격력 증가")
//                             .icon("icon1").tier(1).level(5).build(),
//                     ArkPassiveEffectHistory.builder().category("도약").effectName("치명 증가")
//                             .icon("icon2").tier(2).level(3).build(),
//                     ArkPassiveEffectHistory.builder().category("진화").effectName("이동속도 증가")
//                             .icon("icon3").tier(3).level(2).build()
//             );
// 
//             history.replaceArkPassiveEffects(newEffects);
// 
//             assertThat(history.getArkPassiveEffects()).hasSize(3);
//             assertThat(history.getArkPassiveEffects().get(0).getCategory()).isEqualTo("깨달음");
//             assertThat(history.getArkPassiveEffects().get(0).getTier()).isEqualTo(1);
//             assertThat(history.getArkPassiveEffects().get(0).getCombatPowerHistory()).isEqualTo(history);
//         }
// 
//         @Test
//         @DisplayName("기존 효과를 새 효과로 교체")
//         void replaceExisting() {
//             ArkPassiveEffectHistory old = ArkPassiveEffectHistory.builder()
//                     .category("깨달음").effectName("구 효과").tier(1).level(1).build();
//             old.setCombatPowerHistory(history);
//             history.getArkPassiveEffects().add(old);
// 
//             history.replaceArkPassiveEffects(List.of(
//                     ArkPassiveEffectHistory.builder()
//                             .category("도약").effectName("신 효과").tier(2).level(5).build()
//             ));
// 
//             assertThat(history.getArkPassiveEffects()).hasSize(1);
//             assertThat(history.getArkPassiveEffects().get(0).getCategory()).isEqualTo("도약");
//             assertThat(history.getArkPassiveEffects().get(0).getEffectName()).isEqualTo("신 효과");
//         }
//     }
// 
//     @Nested
//     @DisplayName("모든 컬렉션 동시 교체")
//     class ReplaceAllCollectionsTest {
// 
//         @Test
//         @DisplayName("8개 컬렉션 모두 한 번에 교체해도 정상 동작")
//         void replaceAllAtOnce() {
//             // when
//             history.replaceArkgridEffects(List.of(
//                     ArkgridEffectHistory.builder().effectName("효과1").effectLevel(1).build()
//             ));
//             history.replaceEquipments(List.of(
//                     createEquipment("무기", "+25 무기")
//             ));
//             history.replaceEngravings(List.of(
//                     EngravingHistory.builder().name("원한").level(3).build()
//             ));
//             history.replaceCards(List.of(
//                     CardHistory.builder().slot(0).name("카드1").build()
//             ));
//             history.replaceCardSetEffects(List.of(
//                     CardSetEffectHistory.builder().name("세트1").build()
//             ));
//             history.replaceGems(List.of(
//                     GemHistory.builder().skillName("스킬1").gemSlot(0).level(10).build()
//             ));
//             history.replaceArkPassivePoints(List.of(
//                     ArkPassivePointHistory.builder().name("깨달음").value(30).build()
//             ));
//             history.replaceArkPassiveEffects(List.of(
//                     ArkPassiveEffectHistory.builder().category("깨달음").effectName("효과").tier(1).level(5).build()
//             ));
// 
//             // then
//             assertThat(history.getArkgridEffects()).hasSize(1);
//             assertThat(history.getEquipments()).hasSize(1);
//             assertThat(history.getEngravings()).hasSize(1);
//             assertThat(history.getCards()).hasSize(1);
//             assertThat(history.getCardSetEffects()).hasSize(1);
//             assertThat(history.getGems()).hasSize(1);
//             assertThat(history.getArkPassivePoints()).hasSize(1);
//             assertThat(history.getArkPassiveEffects()).hasSize(1);
//         }
//     }
// 
//     private static EquipmentHistory createEquipment(String type, String name) {
//         EquipmentHistory eq = new EquipmentHistory();
//         eq.setType(type);
//         eq.setName(name);
//         return eq;
//     }
// }
