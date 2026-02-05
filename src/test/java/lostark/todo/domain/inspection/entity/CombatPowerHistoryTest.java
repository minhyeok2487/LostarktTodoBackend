package lostark.todo.domain.inspection.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CombatPowerHistoryTest {

    private CombatPowerHistory history;

    @BeforeEach
    void setUp() {
        history = CombatPowerHistory.builder()
                .id(1L)
                .recordDate(LocalDate.now())
                .combatPower(2000.0)
                .itemLevel(1600.0)
                .characterImage("https://img.test.com/old.png")
                .arkgridEffects(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("updateData 메서드")
    class UpdateDataTest {

        @Test
        @DisplayName("성공 - 데이터 업데이트")
        void success() {
            // when
            history.updateData(2200.0, 1620.0, "https://img.test.com/new.png");

            // then
            assertThat(history.getCombatPower()).isEqualTo(2200.0);
            assertThat(history.getItemLevel()).isEqualTo(1620.0);
            assertThat(history.getCharacterImage()).isEqualTo("https://img.test.com/new.png");
        }
    }

    @Nested
    @DisplayName("replaceArkgridEffects 메서드")
    class ReplaceArkgridEffectsTest {

        @Test
        @DisplayName("성공 - 빈 리스트에 효과 추가")
        void success_addToEmpty() {
            // given
            List<ArkgridEffectHistory> newEffects = List.of(
                    ArkgridEffectHistory.builder()
                            .effectName("공격력 증가")
                            .effectLevel(5)
                            .effectTooltip("공격력이 증가합니다.")
                            .build(),
                    ArkgridEffectHistory.builder()
                            .effectName("방어력 증가")
                            .effectLevel(3)
                            .effectTooltip("방어력이 증가합니다.")
                            .build()
            );

            // when
            history.replaceArkgridEffects(newEffects);

            // then
            assertThat(history.getArkgridEffects()).hasSize(2);
            assertThat(history.getArkgridEffects().get(0).getEffectName()).isEqualTo("공격력 증가");
            assertThat(history.getArkgridEffects().get(1).getEffectName()).isEqualTo("방어력 증가");
            // 부모 참조 설정 확인
            assertThat(history.getArkgridEffects().get(0).getCombatPowerHistory()).isEqualTo(history);
        }

        @Test
        @DisplayName("성공 - 기존 효과를 새 효과로 교체")
        void success_replaceExisting() {
            // given - 기존 효과 추가
            ArkgridEffectHistory existingEffect = ArkgridEffectHistory.builder()
                    .effectName("이전 효과")
                    .effectLevel(1)
                    .build();
            existingEffect.setCombatPowerHistory(history);
            history.getArkgridEffects().add(existingEffect);

            List<ArkgridEffectHistory> newEffects = List.of(
                    ArkgridEffectHistory.builder()
                            .effectName("새 효과")
                            .effectLevel(10)
                            .build()
            );

            // when
            history.replaceArkgridEffects(newEffects);

            // then
            assertThat(history.getArkgridEffects()).hasSize(1);
            assertThat(history.getArkgridEffects().get(0).getEffectName()).isEqualTo("새 효과");
        }

        @Test
        @DisplayName("성공 - 빈 리스트로 교체하면 모든 효과 제거")
        void success_clearWithEmptyList() {
            // given
            ArkgridEffectHistory existingEffect = ArkgridEffectHistory.builder()
                    .effectName("기존 효과")
                    .effectLevel(1)
                    .build();
            existingEffect.setCombatPowerHistory(history);
            history.getArkgridEffects().add(existingEffect);

            // when
            history.replaceArkgridEffects(new ArrayList<>());

            // then
            assertThat(history.getArkgridEffects()).isEmpty();
        }
    }
}
