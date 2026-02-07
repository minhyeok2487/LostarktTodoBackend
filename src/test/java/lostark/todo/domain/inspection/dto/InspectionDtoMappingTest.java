package lostark.todo.domain.inspection.dto;

import lostark.todo.domain.inspection.entity.*;
import lostark.todo.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Inspection DTO 매핑 테스트")
class InspectionDtoMappingTest {

    @Nested
    @DisplayName("InspectionCharacterResponse.from()")
    class InspectionCharacterResponseFromTest {

        @Test
        @DisplayName("엔티티의 모든 필드가 DTO로 매핑된다")
        void allFieldsMapped() {
            // given
            InspectionCharacter entity = InspectionCharacter.builder()
                    .id(1L)
                    .member(Member.builder().id(1L).build())
                    .characterName("테스트캐릭터")
                    .serverName("루페온")
                    .characterClassName("버서커")
                    .characterImage("https://img.test.com/char.png")
                    .itemLevel(1640.0)
                    .combatPower(2350.0)
                    .title("모험가")
                    .guildName("테스트길드")
                    .townName("테스트영지")
                    .townLevel(60)
                    .expeditionLevel(250)
                    .noChangeThreshold(5)
                    .isActive(true)
                    .histories(new ArrayList<>())
                    .build();

            // when
            InspectionCharacterResponse response = InspectionCharacterResponse.from(entity);

            // then
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getCharacterName()).isEqualTo("테스트캐릭터");
            assertThat(response.getServerName()).isEqualTo("루페온");
            assertThat(response.getCharacterClassName()).isEqualTo("버서커");
            assertThat(response.getCharacterImage()).isEqualTo("https://img.test.com/char.png");
            assertThat(response.getItemLevel()).isEqualTo(1640.0);
            assertThat(response.getCombatPower()).isEqualTo(2350.0);
            assertThat(response.getTitle()).isEqualTo("모험가");
            assertThat(response.getGuildName()).isEqualTo("테스트길드");
            assertThat(response.getTownName()).isEqualTo("테스트영지");
            assertThat(response.getTownLevel()).isEqualTo(60);
            assertThat(response.getExpeditionLevel()).isEqualTo(250);
            assertThat(response.getNoChangeThreshold()).isEqualTo(5);
            assertThat(response.isActive()).isTrue();
        }

        @Test
        @DisplayName("nullable 필드가 null인 경우에도 매핑된다")
        void nullableFieldsMapped() {
            // given
            InspectionCharacter entity = InspectionCharacter.builder()
                    .id(2L)
                    .characterName("최소캐릭터")
                    .itemLevel(1600.0)
                    .combatPower(2000.0)
                    .noChangeThreshold(3)
                    .isActive(false)
                    .histories(new ArrayList<>())
                    .build();

            // when
            InspectionCharacterResponse response = InspectionCharacterResponse.from(entity);

            // then
            assertThat(response.getCharacterName()).isEqualTo("최소캐릭터");
            assertThat(response.getServerName()).isNull();
            assertThat(response.getTitle()).isNull();
            assertThat(response.getGuildName()).isNull();
            assertThat(response.getTownName()).isNull();
            assertThat(response.getTownLevel()).isNull();
            assertThat(response.getExpeditionLevel()).isNull();
            assertThat(response.isActive()).isFalse();
        }

        @Test
        @DisplayName("변화량 필드는 기본값 0으로 초기화된다")
        void changeFieldsDefaultToZero() {
            // given
            InspectionCharacter entity = InspectionCharacter.builder()
                    .id(1L)
                    .characterName("테스트")
                    .histories(new ArrayList<>())
                    .build();

            // when
            InspectionCharacterResponse response = InspectionCharacterResponse.from(entity);

            // then
            assertThat(response.getCombatPowerChange()).isEqualTo(0.0);
            assertThat(response.getItemLevelChange()).isEqualTo(0.0);
            assertThat(response.getUnchangedDays()).isEqualTo(0L);
            assertThat(response.getPreviousCombatPower()).isNull();
            assertThat(response.getPreviousItemLevel()).isNull();
        }
    }

    @Nested
    @DisplayName("CombatPowerHistoryResponse.from()")
    class CombatPowerHistoryResponseFromTest {

        @Test
        @DisplayName("모든 필드와 8개 컬렉션이 매핑된다")
        void allFieldsAndCollectionsMapped() {
            // given
            CombatPowerHistory entity = CombatPowerHistory.builder()
                    .id(1L)
                    .recordDate(LocalDate.of(2025, 1, 15))
                    .combatPower(2350.0)
                    .itemLevel(1640.0)
                    .statsJson("[{\"type\":\"치명\",\"value\":\"800\"}]")
                    .arkPassiveTitle("아크 타이틀")
                    .townName("영지")
                    .townLevel(60)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            // 아크그리드 효과 추가
            ArkgridEffectHistory ae = ArkgridEffectHistory.builder()
                    .id(1L).effectName("공격력 증가").effectLevel(5).effectTooltip("tooltip").build();
            ae.setCombatPowerHistory(entity);
            entity.getArkgridEffects().add(ae);

            // 장비 추가
            EquipmentHistory eq = new EquipmentHistory();
            eq.setId(1L);
            eq.setType("무기");
            eq.setName("+25 롱 스태프");
            eq.setGrade("에스더");
            eq.setRefinement(25);
            eq.setCombatPowerHistory(entity);
            entity.getEquipments().add(eq);

            // 각인 추가
            EngravingHistory eng = EngravingHistory.builder()
                    .id(1L).name("원한").level(3).grade("유물").description("피해 +20%").build();
            eng.setCombatPowerHistory(entity);
            entity.getEngravings().add(eng);

            // 카드 추가
            CardHistory card = CardHistory.builder()
                    .id(1L).slot(0).name("카드1").icon("icon").awakeCount(5).awakeTotal(5).grade("전설").build();
            card.setCombatPowerHistory(entity);
            entity.getCards().add(card);

            // 카드 세트효과 추가
            CardSetEffectHistory cse = CardSetEffectHistory.builder()
                    .id(1L).name("세트1").description("피해 +7%").build();
            cse.setCombatPowerHistory(entity);
            entity.getCardSetEffects().add(cse);

            // 보석 추가
            GemHistory gem = GemHistory.builder()
                    .id(1L).skillName("잔혈 폭격").gemSlot(0).level(10).grade("유물")
                    .description("피해 +21%").option("멸화").build();
            gem.setCombatPowerHistory(entity);
            entity.getGems().add(gem);

            // 아크패시브 포인트 추가
            ArkPassivePointHistory point = ArkPassivePointHistory.builder()
                    .id(1L).name("깨달음").value(30).description("포인트").build();
            point.setCombatPowerHistory(entity);
            entity.getArkPassivePoints().add(point);

            // 아크패시브 효과 추가
            ArkPassiveEffectHistory effect = ArkPassiveEffectHistory.builder()
                    .id(1L).category("깨달음").effectName("공격력").icon("icon").tier(1).level(5).build();
            effect.setCombatPowerHistory(entity);
            entity.getArkPassiveEffects().add(effect);

            // when
            CombatPowerHistoryResponse response = CombatPowerHistoryResponse.from(entity);

            // then - 기본 필드
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getRecordDate()).isEqualTo(LocalDate.of(2025, 1, 15));
            assertThat(response.getCombatPower()).isEqualTo(2350.0);
            assertThat(response.getItemLevel()).isEqualTo(1640.0);
            assertThat(response.getStatsJson()).contains("치명");
            assertThat(response.getArkPassiveTitle()).isEqualTo("아크 타이틀");
            assertThat(response.getTownName()).isEqualTo("영지");
            assertThat(response.getTownLevel()).isEqualTo(60);

            // 컬렉션 매핑 검증
            assertThat(response.getArkgridEffects()).hasSize(1);
            assertThat(response.getEquipments()).hasSize(1);
            assertThat(response.getEngravings()).hasSize(1);
            assertThat(response.getCards()).hasSize(1);
            assertThat(response.getCardSetEffects()).hasSize(1);
            assertThat(response.getGems()).hasSize(1);
            assertThat(response.getArkPassivePoints()).hasSize(1);
            assertThat(response.getArkPassiveEffects()).hasSize(1);
        }

        @Test
        @DisplayName("빈 컬렉션은 빈 리스트로 매핑된다")
        void emptyCollections_mappedToEmptyLists() {
            // given
            CombatPowerHistory entity = CombatPowerHistory.builder()
                    .id(2L)
                    .recordDate(LocalDate.now())
                    .combatPower(2000.0)
                    .itemLevel(1600.0)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            // when
            CombatPowerHistoryResponse response = CombatPowerHistoryResponse.from(entity);

            // then
            assertThat(response.getArkgridEffects()).isEmpty();
            assertThat(response.getEquipments()).isEmpty();
            assertThat(response.getEngravings()).isEmpty();
            assertThat(response.getCards()).isEmpty();
            assertThat(response.getCardSetEffects()).isEmpty();
            assertThat(response.getGems()).isEmpty();
            assertThat(response.getArkPassivePoints()).isEmpty();
            assertThat(response.getArkPassiveEffects()).isEmpty();
        }

        @Test
        @DisplayName("nullable 필드가 null인 경우에도 매핑된다")
        void nullableFieldsMapped() {
            // given
            CombatPowerHistory entity = CombatPowerHistory.builder()
                    .id(3L)
                    .recordDate(LocalDate.now())
                    .combatPower(2000.0)
                    .itemLevel(1600.0)
                    .statsJson(null)
                    .arkPassiveTitle(null)
                    .townName(null)
                    .townLevel(null)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            // when
            CombatPowerHistoryResponse response = CombatPowerHistoryResponse.from(entity);

            // then
            assertThat(response.getStatsJson()).isNull();
            assertThat(response.getArkPassiveTitle()).isNull();
            assertThat(response.getTownName()).isNull();
            assertThat(response.getTownLevel()).isNull();
        }
    }

    @Nested
    @DisplayName("InspectionDashboardResponse")
    class DashboardResponseTest {

        @Test
        @DisplayName("캐릭터 + 히스토리 목록이 조합된다")
        void buildsCorrectly() {
            // given
            InspectionCharacterResponse charResp = InspectionCharacterResponse.builder()
                    .id(1L).characterName("테스트").build();

            CombatPowerHistoryResponse hist1 = CombatPowerHistoryResponse.builder()
                    .id(1L).recordDate(LocalDate.now().minusDays(1)).combatPower(2200.0).build();
            CombatPowerHistoryResponse hist2 = CombatPowerHistoryResponse.builder()
                    .id(2L).recordDate(LocalDate.now()).combatPower(2350.0).build();

            // when
            InspectionDashboardResponse dashboard = InspectionDashboardResponse.builder()
                    .character(charResp)
                    .histories(List.of(hist1, hist2))
                    .build();

            // then
            assertThat(dashboard.getCharacter().getCharacterName()).isEqualTo("테스트");
            assertThat(dashboard.getHistories()).hasSize(2);
            assertThat(dashboard.getHistories().get(0).getCombatPower()).isEqualTo(2200.0);
            assertThat(dashboard.getHistories().get(1).getCombatPower()).isEqualTo(2350.0);
        }
    }

    @Nested
    @DisplayName("개별 히스토리 Response 매핑")
    class IndividualHistoryResponseTest {

        @Test
        @DisplayName("EngravingHistoryResponse 매핑")
        void engravingHistoryResponseMapping() {
            EngravingHistory entity = EngravingHistory.builder()
                    .id(1L).name("원한").level(3).grade("유물")
                    .abilityStoneLevel(7).description("피해 +20%").build();

            EngravingHistoryResponse response = EngravingHistoryResponse.from(entity);

            assertThat(response.getName()).isEqualTo("원한");
            assertThat(response.getLevel()).isEqualTo(3);
            assertThat(response.getGrade()).isEqualTo("유물");
            assertThat(response.getAbilityStoneLevel()).isEqualTo(7);
            assertThat(response.getDescription()).isEqualTo("피해 +20%");
        }

        @Test
        @DisplayName("CardHistoryResponse 매핑")
        void cardHistoryResponseMapping() {
            CardHistory entity = CardHistory.builder()
                    .id(1L).slot(2).name("카드3").icon("icon3")
                    .awakeCount(3).awakeTotal(5).grade("전설").build();

            CardHistoryResponse response = CardHistoryResponse.from(entity);

            assertThat(response.getSlot()).isEqualTo(2);
            assertThat(response.getName()).isEqualTo("카드3");
            assertThat(response.getAwakeCount()).isEqualTo(3);
            assertThat(response.getAwakeTotal()).isEqualTo(5);
        }

        @Test
        @DisplayName("GemHistoryResponse 매핑")
        void gemHistoryResponseMapping() {
            GemHistory entity = GemHistory.builder()
                    .id(1L).skillName("잔혈 폭격").gemSlot(0)
                    .level(10).grade("유물").description("피해 +21%").option("멸화").build();

            GemHistoryResponse response = GemHistoryResponse.from(entity);

            assertThat(response.getSkillName()).isEqualTo("잔혈 폭격");
            assertThat(response.getGemSlot()).isEqualTo(0);
            assertThat(response.getLevel()).isEqualTo(10);
            assertThat(response.getOption()).isEqualTo("멸화");
        }

        @Test
        @DisplayName("ArkPassivePointHistoryResponse 매핑")
        void arkPassivePointResponseMapping() {
            ArkPassivePointHistory entity = ArkPassivePointHistory.builder()
                    .id(1L).name("깨달음").value(30).description("포인트").build();

            ArkPassivePointHistoryResponse response = ArkPassivePointHistoryResponse.from(entity);

            assertThat(response.getName()).isEqualTo("깨달음");
            assertThat(response.getValue()).isEqualTo(30);
        }

        @Test
        @DisplayName("ArkPassiveEffectHistoryResponse 매핑")
        void arkPassiveEffectResponseMapping() {
            ArkPassiveEffectHistory entity = ArkPassiveEffectHistory.builder()
                    .id(1L).category("도약").effectName("치명 증가")
                    .icon("icon").tier(2).level(3).build();

            ArkPassiveEffectHistoryResponse response = ArkPassiveEffectHistoryResponse.from(entity);

            assertThat(response.getCategory()).isEqualTo("도약");
            assertThat(response.getEffectName()).isEqualTo("치명 증가");
            assertThat(response.getTier()).isEqualTo(2);
            assertThat(response.getLevel()).isEqualTo(3);
        }

        @Test
        @DisplayName("CardSetEffectHistoryResponse 매핑")
        void cardSetEffectResponseMapping() {
            CardSetEffectHistory entity = CardSetEffectHistory.builder()
                    .id(1L).name("남겨진 바람의 절벽 6세트").description("암속성 피해 감소 +30%").build();

            CardSetEffectHistoryResponse response = CardSetEffectHistoryResponse.from(entity);

            assertThat(response.getName()).contains("바람의 절벽");
            assertThat(response.getDescription()).contains("+30%");
        }

        @Test
        @DisplayName("ArkgridEffectResponse 매핑")
        void arkgridEffectResponseMapping() {
            ArkgridEffectHistory entity = ArkgridEffectHistory.builder()
                    .id(1L).effectName("공격력 증가").effectLevel(5).effectTooltip("공격력이 증가합니다.").build();

            ArkgridEffectResponse response = ArkgridEffectResponse.from(entity);

            assertThat(response.getEffectName()).isEqualTo("공격력 증가");
            assertThat(response.getEffectLevel()).isEqualTo(5);
        }
    }
}
