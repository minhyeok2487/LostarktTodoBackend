package lostark.todo.domain.member.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Member 생기 물약 카운트 테스트")
class MemberLifePotionTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .username("testuser")
                .lifePotionSmall(0)
                .lifePotionMedium(0)
                .lifePotionLarge(0)
                .build();
    }

    @Nested
    @DisplayName("소형 생기 물약 테스트")
    class SmallPotionTest {

        @Test
        @DisplayName("소형 생기 물약 추가")
        void updateLifePotionSmall_positive() {
            // when
            member.updateLifePotionSmall(5);

            // then
            assertThat(member.getLifePotionSmall()).isEqualTo(5);
        }

        @Test
        @DisplayName("소형 생기 물약 감소")
        void updateLifePotionSmall_negative() {
            // given
            member.updateLifePotionSmall(10);

            // when
            member.updateLifePotionSmall(-3);

            // then
            assertThat(member.getLifePotionSmall()).isEqualTo(7);
        }

        @Test
        @DisplayName("소형 생기 물약은 0 이하로 감소하지 않는다")
        void updateLifePotionSmall_notBelowZero() {
            // given
            member.updateLifePotionSmall(2);

            // when
            member.updateLifePotionSmall(-10);

            // then
            assertThat(member.getLifePotionSmall()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("중형 생기 물약 테스트")
    class MediumPotionTest {

        @Test
        @DisplayName("중형 생기 물약 추가")
        void updateLifePotionMedium_positive() {
            // when
            member.updateLifePotionMedium(5);

            // then
            assertThat(member.getLifePotionMedium()).isEqualTo(5);
        }

        @Test
        @DisplayName("중형 생기 물약 감소")
        void updateLifePotionMedium_negative() {
            // given
            member.updateLifePotionMedium(10);

            // when
            member.updateLifePotionMedium(-3);

            // then
            assertThat(member.getLifePotionMedium()).isEqualTo(7);
        }

        @Test
        @DisplayName("중형 생기 물약은 0 이하로 감소하지 않는다")
        void updateLifePotionMedium_notBelowZero() {
            // given
            member.updateLifePotionMedium(2);

            // when
            member.updateLifePotionMedium(-10);

            // then
            assertThat(member.getLifePotionMedium()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("대형 생기 물약 테스트")
    class LargePotionTest {

        @Test
        @DisplayName("대형 생기 물약 추가")
        void updateLifePotionLarge_positive() {
            // when
            member.updateLifePotionLarge(5);

            // then
            assertThat(member.getLifePotionLarge()).isEqualTo(5);
        }

        @Test
        @DisplayName("대형 생기 물약 감소")
        void updateLifePotionLarge_negative() {
            // given
            member.updateLifePotionLarge(10);

            // when
            member.updateLifePotionLarge(-3);

            // then
            assertThat(member.getLifePotionLarge()).isEqualTo(7);
        }

        @Test
        @DisplayName("대형 생기 물약은 0 이하로 감소하지 않는다")
        void updateLifePotionLarge_notBelowZero() {
            // given
            member.updateLifePotionLarge(2);

            // when
            member.updateLifePotionLarge(-10);

            // then
            assertThat(member.getLifePotionLarge()).isEqualTo(0);
        }
    }
}
