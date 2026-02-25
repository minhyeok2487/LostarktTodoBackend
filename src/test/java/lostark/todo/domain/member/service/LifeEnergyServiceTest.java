package lostark.todo.domain.member.service;

import lostark.todo.domain.member.dto.UpdateLifePotionsRequest;
import lostark.todo.domain.member.dto.UsePotionRequest;
import lostark.todo.domain.member.entity.LifeEnergy;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.enums.PotionType;
import lostark.todo.domain.member.repository.LifeEnergyRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LifeEnergyService 테스트")
class LifeEnergyServiceTest {

    @Mock
    private LifeEnergyRepository repository;

    @InjectMocks
    private LifeEnergyService service;

    private Member member;
    private LifeEnergy lifeEnergy;
    private static final String USERNAME = "test@test.com";
    private static final Long LIFE_ENERGY_ID = 1L;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .username(USERNAME)
                .build();

        lifeEnergy = LifeEnergy.builder()
                .energy(5000)
                .maxEnergy(10000)
                .characterName("테스트캐릭")
                .beatrice(false)
                .build();
        // 리플렉션으로 id, member 설정 (Builder로 설정 불가한 필드)
        try {
            var idField = LifeEnergy.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(lifeEnergy, LIFE_ENERGY_ID);

            var memberField = LifeEnergy.class.getDeclaredField("member");
            memberField.setAccessible(true);
            memberField.set(lifeEnergy, member);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("updatePotions - 물약 일괄 저장")
    class UpdatePotionsTest {

        @Test
        @DisplayName("4종 물약 수량을 한번에 설정한다")
        void updateAllPotions() {
            // given
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UpdateLifePotionsRequest request = UpdateLifePotionsRequest.builder()
                    .lifeEnergyId(LIFE_ENERGY_ID)
                    .potionLeap(5)
                    .potionSmall(3)
                    .potionMedium(2)
                    .potionLarge(1)
                    .build();

            // when
            service.updatePotions(USERNAME, request);

            // then
            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(5);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(3);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(2);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(1);
        }

        @Test
        @DisplayName("음수 입력 시 0으로 보정된다")
        void negativeValuesCorrectedToZero() {
            // given
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UpdateLifePotionsRequest request = UpdateLifePotionsRequest.builder()
                    .lifeEnergyId(LIFE_ENERGY_ID)
                    .potionLeap(-1)
                    .potionSmall(-5)
                    .potionMedium(0)
                    .potionLarge(10)
                    .build();

            // when
            service.updatePotions(USERNAME, request);

            // then
            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(0);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(10);
        }

        @Test
        @DisplayName("기존 수량을 새 값으로 덮어쓴다")
        void overwriteExistingValues() {
            // given
            lifeEnergy.updatePotionCount(PotionType.LEAP, 10);
            lifeEnergy.updatePotionCount(PotionType.SMALL, 20);
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));

            UpdateLifePotionsRequest request = UpdateLifePotionsRequest.builder()
                    .lifeEnergyId(LIFE_ENERGY_ID)
                    .potionLeap(1)
                    .potionSmall(2)
                    .potionMedium(3)
                    .potionLarge(4)
                    .build();

            // when
            service.updatePotions(USERNAME, request);

            // then
            assertThat(lifeEnergy.getPotionLeap()).isEqualTo(1);
            assertThat(lifeEnergy.getPotionSmall()).isEqualTo(2);
            assertThat(lifeEnergy.getPotionMedium()).isEqualTo(3);
            assertThat(lifeEnergy.getPotionLarge()).isEqualTo(4);
        }

        @Test
        @DisplayName("존재하지 않는 lifeEnergyId면 예외 발생")
        void notFound() {
            // given
            given(repository.findById(999L)).willReturn(Optional.empty());
            UpdateLifePotionsRequest request = UpdateLifePotionsRequest.builder()
                    .lifeEnergyId(999L)
                    .potionLeap(0)
                    .potionSmall(0)
                    .potionMedium(0)
                    .potionLarge(0)
                    .build();

            // when & then
            assertThatThrownBy(() -> service.updatePotions(USERNAME, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("데이터가 존재하지 않습니다");
        }

        @Test
        @DisplayName("다른 사용자의 데이터에 접근하면 권한 예외 발생")
        void unauthorized() {
            // given
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UpdateLifePotionsRequest request = UpdateLifePotionsRequest.builder()
                    .lifeEnergyId(LIFE_ENERGY_ID)
                    .potionLeap(0)
                    .potionSmall(0)
                    .potionMedium(0)
                    .potionLarge(0)
                    .build();

            // when & then
            assertThatThrownBy(() -> service.updatePotions("other@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("권한이 없습니다");
        }
    }

    @Nested
    @DisplayName("usePotion - 물약 사용")
    class UsePotionTest {

        @Test
        @DisplayName("생명의 물약(소) 사용 시 energy += 1000, 재고 -1")
        void useSmallPotion() {
            // given
            lifeEnergy.updatePotionCount(PotionType.SMALL, 3);
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UsePotionRequest request = new UsePotionRequest();
            request.setLifeEnergyId(LIFE_ENERGY_ID);
            request.setType(PotionType.SMALL);

            // when
            LifeEnergy result = service.usePotion(USERNAME, request);

            // then
            assertThat(result.getEnergy()).isEqualTo(6000);
            assertThat(result.getPotionSmall()).isEqualTo(2);
        }

        @Test
        @DisplayName("생명의 물약(대) 사용 시 maxEnergy 초과 가능")
        void exceedMaxEnergy() {
            // given
            lifeEnergy.updatePotionCount(PotionType.LARGE, 2);
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UsePotionRequest request = new UsePotionRequest();
            request.setLifeEnergyId(LIFE_ENERGY_ID);
            request.setType(PotionType.LARGE);

            // when
            service.usePotion(USERNAME, request);
            LifeEnergy result = service.usePotion(USERNAME, request);

            // then
            assertThat(result.getEnergy()).isEqualTo(15000); // 5000 + 5000 + 5000
            assertThat(result.getEnergy()).isGreaterThan(result.getMaxEnergy());
        }

        @Test
        @DisplayName("재고 없으면 예외 발생")
        void noStock() {
            // given
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UsePotionRequest request = new UsePotionRequest();
            request.setLifeEnergyId(LIFE_ENERGY_ID);
            request.setType(PotionType.SMALL);

            // when & then
            assertThatThrownBy(() -> service.usePotion(USERNAME, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("물약이 부족합니다");
        }

        @Test
        @DisplayName("도약의 물약 사용 시 재고만 차감, energy 변화 없음")
        void useLeapPotion() {
            // given
            lifeEnergy.updatePotionCount(PotionType.LEAP, 5);
            given(repository.findById(LIFE_ENERGY_ID)).willReturn(Optional.of(lifeEnergy));
            UsePotionRequest request = new UsePotionRequest();
            request.setLifeEnergyId(LIFE_ENERGY_ID);
            request.setType(PotionType.LEAP);

            // when
            LifeEnergy result = service.usePotion(USERNAME, request);

            // then
            assertThat(result.getPotionLeap()).isEqualTo(4);
            assertThat(result.getEnergy()).isEqualTo(5000); // 변화 없음
        }
    }
}
