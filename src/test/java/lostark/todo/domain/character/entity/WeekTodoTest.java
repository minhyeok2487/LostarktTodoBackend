package lostark.todo.domain.character.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("WeekTodo 엔티티 테스트")
class WeekTodoTest {

    private WeekTodo weekTodo;

    @BeforeEach
    void setUp() {
        weekTodo = WeekTodo.builder()
                .weekEpona(0)
                .silmaelChange(false)
                .cubeTicket(0)
                .elysianCount(0)
                .hellKey(0)
                .build();
    }

    @Nested
    @DisplayName("지옥 열쇠 카운트 테스트")
    class HellKeyTest {

        @Test
        @DisplayName("지옥 열쇠 추가 - 양수 값을 더하면 증가한다")
        void updateHellKey_positive() {
            // given
            int initialCount = weekTodo.getHellKey();

            // when
            weekTodo.updateHellKey(3);

            // then
            assertThat(weekTodo.getHellKey()).isEqualTo(initialCount + 3);
        }

        @Test
        @DisplayName("지옥 열쇠 감소 - 음수 값을 더하면 감소한다")
        void updateHellKey_negative() {
            // given
            weekTodo.updateHellKey(5); // 먼저 5개 추가

            // when
            weekTodo.updateHellKey(-2);

            // then
            assertThat(weekTodo.getHellKey()).isEqualTo(3);
        }

        @Test
        @DisplayName("지옥 열쇠 감소 - 0 이하로 내려가면 0으로 유지된다")
        void updateHellKey_notBelowZero() {
            // given
            weekTodo.updateHellKey(2);

            // when
            weekTodo.updateHellKey(-5);

            // then
            assertThat(weekTodo.getHellKey()).isEqualTo(0);
        }

        @Test
        @DisplayName("지옥 열쇠 초기값은 0이다")
        void hellKey_initialValue() {
            // given
            WeekTodo newWeekTodo = new WeekTodo();

            // then
            assertThat(newWeekTodo.getHellKey()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("할의 모래시계 테스트")
    class HalHourglassTest {

        @Test
        @DisplayName("할의 모래시계 토글 - false에서 true로 변경된다")
        void updateHalHourglass_toggle() {
            // given
            assertThat(weekTodo.isHalHourglass()).isFalse();

            // when
            weekTodo.updateHalHourglass();

            // then
            assertThat(weekTodo.isHalHourglass()).isTrue();
        }

        @Test
        @DisplayName("할의 모래시계 토글 - true에서 false로 변경된다")
        void updateHalHourglass_toggleBack() {
            // given
            weekTodo.updateHalHourglass(); // false -> true
            assertThat(weekTodo.isHalHourglass()).isTrue();

            // when
            weekTodo.updateHalHourglass(); // true -> false

            // then
            assertThat(weekTodo.isHalHourglass()).isFalse();
        }

        @Test
        @DisplayName("할의 모래시계 초기값은 false이다")
        void halHourglass_initialValue() {
            // given
            WeekTodo newWeekTodo = new WeekTodo();

            // then
            assertThat(newWeekTodo.isHalHourglass()).isFalse();
        }
    }
}
