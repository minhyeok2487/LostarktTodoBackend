package lostark.todo.domain.member.entity;

import lostark.todo.domain.member.enums.PotionType;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LifeEnergy 물약 테스트")
class LifeEnergyPotionTest {

    private LifeEnergy lifeEnergy;

    @BeforeEach
    void setUp() {
        lifeEnergy = LifeEnergy.builder()
                .energy(5000)
                .maxEnergy(10000)
                .characterName("테스트캐릭")
                .beatrice(false)
                .build();
    }

    @Nested
    @DisplayName("PotionType enum")
    class PotionTypeTest {

        @Test
        @DisplayName("각 물약의 회복량이 올바르다")
        void recoveryAmounts() {
            assertThat(PotionType.LEAP.getRecoveryAmount()).isEqualTo(0);
            assertThat(PotionType.SMALL.getRecoveryAmount()).isEqualTo(1000);
            assertThat(PotionType.MEDIUM.getRecoveryAmount()).isEqualTo(3000);
            assertThat(PotionType.LARGE.getRecoveryAmount()).isEqualTo(5000);
        }
    }

    @Nested
    @DisplayName("updatePotionCount")
    class UpdatePotionCountTest {

        @Test
        @DisplayName("물약 재고 추가")
        void addPotion() {
            lifeEnergy.updatePotionCount(PotionType.SMALL, 3);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(3);
        }

        @Test
        @DisplayName("물약 재고 차감")
        void subtractPotion() {
            lifeEnergy.updatePotionCount(PotionType.MEDIUM, 5);
            lifeEnergy.updatePotionCount(PotionType.MEDIUM, -2);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(3);
        }

        @Test
        @DisplayName("물약 재고는 0 미만으로 내려가지 않는다")
        void notBelowZero() {
            lifeEnergy.updatePotionCount(PotionType.LARGE, 1);
            lifeEnergy.updatePotionCount(PotionType.LARGE, -5);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(0);
        }

        @Test
        @DisplayName("도약의 물약 재고 관리")
        void leapPotion() {
            lifeEnergy.updatePotionCount(PotionType.LEAP, 2);
            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("usePotion - 물약 사용")
    class UsePotionTest {

        @Test
        @DisplayName("생명의 물약(소) 사용 시 energy += 1000, 재고 -1")
        void useSmall() {
            lifeEnergy.updatePotionCount(PotionType.SMALL, 3);

            lifeEnergy.usePotion(PotionType.SMALL);

            assertThat(lifeEnergy.getEnergy()).isEqualTo(6000);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(2);
        }

        @Test
        @DisplayName("생명의 물약(중) 사용 시 energy += 3000")
        void useMedium() {
            lifeEnergy.updatePotionCount(PotionType.MEDIUM, 1);

            lifeEnergy.usePotion(PotionType.MEDIUM);

            assertThat(lifeEnergy.getEnergy()).isEqualTo(8000);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(0);
        }

        @Test
        @DisplayName("생명의 물약(대) 사용 시 maxEnergy를 초과할 수 있다")
        void exceedMaxEnergy() {
            lifeEnergy.updatePotionCount(PotionType.LARGE, 2);

            lifeEnergy.usePotion(PotionType.LARGE); // 5000 + 5000 = 10000
            lifeEnergy.usePotion(PotionType.LARGE); // 10000 + 5000 = 15000

            assertThat(lifeEnergy.getEnergy()).isEqualTo(15000);
            assertThat(lifeEnergy.getEnergy()).isGreaterThan(lifeEnergy.getMaxEnergy());
        }

        @Test
        @DisplayName("재고가 없으면 ConditionNotMetException")
        void noStock() {
            assertThatThrownBy(() -> lifeEnergy.usePotion(PotionType.SMALL))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("물약이 부족합니다");
        }

        @Test
        @DisplayName("도약의 물약 사용 시 재고만 차감되고 energy는 변하지 않는다")
        void useLeap() {
            lifeEnergy.updatePotionCount(PotionType.LEAP, 5);
            int energyBefore = lifeEnergy.getEnergy();

            lifeEnergy.usePotion(PotionType.LEAP);

            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(4);
            assertThat(lifeEnergy.getEnergy()).isEqualTo(energyBefore);
        }
    }

    @Nested
    @DisplayName("updatePotions - 일괄 저장")
    class UpdatePotionsTest {

        @Test
        @DisplayName("4종 물약을 한번에 설정한다")
        void setAll() {
            lifeEnergy.updatePotions(5, 3, 2, 1);

            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(5);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(3);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(2);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(1);
        }

        @Test
        @DisplayName("음수 입력 시 0으로 보정된다")
        void negativeToZero() {
            lifeEnergy.updatePotions(-1, -10, 0, 5);

            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(5);
        }

        @Test
        @DisplayName("기존 값을 완전히 덮어쓴다")
        void overwrite() {
            lifeEnergy.updatePotionCount(PotionType.LEAP, 100);
            lifeEnergy.updatePotionCount(PotionType.SMALL, 200);

            lifeEnergy.updatePotions(1, 2, 3, 4);

            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(1);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("getPotionCount")
    class GetPotionCountTest {

        @Test
        @DisplayName("초기값은 모두 0")
        void initialValues() {
            assertThat(lifeEnergy.getPotionCount(PotionType.LEAP)).isEqualTo(0);
            assertThat(lifeEnergy.getPotionCount(PotionType.SMALL)).isEqualTo(0);
            assertThat(lifeEnergy.getPotionCount(PotionType.MEDIUM)).isEqualTo(0);
            assertThat(lifeEnergy.getPotionCount(PotionType.LARGE)).isEqualTo(0);
        }
    }
}
