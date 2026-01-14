package lostark.todo.domain.schedule.service;

import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.character.repository.RaidBusGoldRepository;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.global.keyvalue.KeyValueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WeekTodoResetServiceTest {

    @Mock
    private KeyValueRepository keyValueRepository;

    @Mock
    private TodoV2Repository todoV2Repository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CustomTodoRepository customTodoRepository;

    @Mock
    private RaidBusGoldRepository raidBusGoldRepository;

    @InjectMocks
    private WeekTodoResetService weekTodoResetService;

    @Nested
    @DisplayName("updateTwoCycle 메서드")
    class UpdateTwoCycleTest {

        @Test
        @DisplayName("성공 - 2주기 체크 토글")
        void success() {
            // given
            given(keyValueRepository.updateTwoCycle()).willReturn(1);

            // when
            int result = weekTodoResetService.updateTwoCycle();

            // then
            assertThat(result).isEqualTo(1);
            verify(keyValueRepository).updateTwoCycle();
        }

        @Test
        @DisplayName("성공 - 2주기 체크 토글 (0으로 변경)")
        void success_toggleToZero() {
            // given
            given(keyValueRepository.updateTwoCycle()).willReturn(0);

            // when
            int result = weekTodoResetService.updateTwoCycle();

            // then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("resetTodoV2CoolTime2 메서드")
    class ResetTodoV2CoolTime2Test {

        @Test
        @DisplayName("성공 - 2주기 레이드 쿨타임 처리")
        void success() {
            // given
            given(todoV2Repository.resetTodoV2CoolTime2()).willReturn(50);

            // when
            long result = weekTodoResetService.resetTodoV2CoolTime2();

            // then
            assertThat(result).isEqualTo(50L);
            verify(todoV2Repository).resetTodoV2CoolTime2();
        }

        @Test
        @DisplayName("성공 - 처리할 2주기 레이드가 없는 경우")
        void success_noTwoWeekRaids() {
            // given
            given(todoV2Repository.resetTodoV2CoolTime2()).willReturn(0);

            // when
            long result = weekTodoResetService.resetTodoV2CoolTime2();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("resetTodoV2 메서드")
    class ResetTodoV2Test {

        @Test
        @DisplayName("성공 - 주간 레이드 초기화")
        void success() {
            // when
            weekTodoResetService.resetTodoV2();

            // then
            verify(todoV2Repository).resetTodoV2();
        }
    }

    @Nested
    @DisplayName("updateWeekContent 메서드")
    class UpdateWeekContentTest {

        @Test
        @DisplayName("성공 - 주간 숙제 초기화")
        void success() {
            // given
            given(characterRepository.updateWeekContent()).willReturn(100);

            // when
            long result = weekTodoResetService.updateWeekContent();

            // then
            assertThat(result).isEqualTo(100L);
            verify(characterRepository).updateWeekContent();
        }

        @Test
        @DisplayName("성공 - 초기화할 주간 숙제가 없는 경우")
        void success_noWeekContent() {
            // given
            given(characterRepository.updateWeekContent()).willReturn(0);

            // when
            long result = weekTodoResetService.updateWeekContent();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("updateWeekDayTodoTotalGold 메서드")
    class UpdateWeekDayTodoTotalGoldTest {

        @Test
        @DisplayName("성공 - 일일 수익 주간 합 초기화")
        void success() {
            // when
            weekTodoResetService.updateWeekDayTodoTotalGold();

            // then
            verify(characterRepository).updateWeekDayTodoTotalGold();
        }
    }

    @Nested
    @DisplayName("updateCustomWeeklyTodo 메서드")
    class UpdateCustomWeeklyTodoTest {

        @Test
        @DisplayName("성공 - 커스텀 주간 숙제 초기화")
        void success() {
            // given
            given(customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY)).willReturn(30L);

            // when
            long result = weekTodoResetService.updateCustomWeeklyTodo();

            // then
            assertThat(result).isEqualTo(30L);
            verify(customTodoRepository).update(CustomTodoFrequencyEnum.WEEKLY);
        }

        @Test
        @DisplayName("성공 - 초기화할 커스텀 주간 숙제가 없는 경우")
        void success_noCustomWeeklyTodos() {
            // given
            given(customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY)).willReturn(0L);

            // when
            long result = weekTodoResetService.updateCustomWeeklyTodo();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("deleteAllRaidBusGold 메서드")
    class DeleteAllRaidBusGoldTest {

        @Test
        @DisplayName("성공 - 버스비 전체 삭제")
        void success() {
            // when
            weekTodoResetService.deleteAllRaidBusGold();

            // then
            verify(raidBusGoldRepository).deleteAllRaidBusGold();
        }
    }
}
