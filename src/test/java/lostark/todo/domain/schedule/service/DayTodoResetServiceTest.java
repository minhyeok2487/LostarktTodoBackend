package lostark.todo.domain.schedule.service;

import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DayTodoResetServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CustomTodoRepository customTodoRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MarketService marketService;

    @Mock
    private ServerTodoStateRepository serverTodoStateRepository;

    @InjectMocks
    private DayTodoResetService dayTodoResetService;

    @Nested
    @DisplayName("updateDayContentGauge 메서드")
    class UpdateDayContentGaugeTest {

        @Test
        @DisplayName("성공 - 휴식 게이지 업데이트")
        void success() {
            // given
            given(characterRepository.updateDayContentGauge()).willReturn(100L);

            // when
            long result = dayTodoResetService.updateDayContentGauge();

            // then
            assertThat(result).isEqualTo(100L);
            verify(characterRepository).updateDayContentGauge();
        }

        @Test
        @DisplayName("성공 - 업데이트할 캐릭터가 없는 경우")
        void success_noCharacters() {
            // given
            given(characterRepository.updateDayContentGauge()).willReturn(0L);

            // when
            long result = dayTodoResetService.updateDayContentGauge();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("saveBeforeGauge 메서드")
    class SaveBeforeGaugeTest {

        @Test
        @DisplayName("성공 - 이전 게이지 저장")
        void success() {
            // given
            given(characterRepository.saveBeforeGauge()).willReturn(150L);

            // when
            long result = dayTodoResetService.saveBeforeGauge();

            // then
            assertThat(result).isEqualTo(150L);
            verify(characterRepository).saveBeforeGauge();
        }
    }

    @Nested
    @DisplayName("updateDayContentCheck 메서드")
    class UpdateDayContentCheckTest {

        @Test
        @DisplayName("성공 - 일일 숙제 체크 초기화")
        void success() {
            // given
            given(characterRepository.updateDayContentCheck()).willReturn(200L);

            // when
            long result = dayTodoResetService.updateDayContentCheck();

            // then
            assertThat(result).isEqualTo(200L);
            verify(characterRepository).updateDayContentCheck();
        }
    }

    @Nested
    @DisplayName("updateDayTodoGold 메서드")
    class UpdateDayTodoGoldTest {

        @Test
        @DisplayName("성공 - 가디언 가격 업데이트 (3티어)")
        void success_tier3() {
            // given
            Map<String, Market> contentResource = new HashMap<>();
            Market tier3Jewelry = Market.builder().recentPrice(100).build();
            Market tier4Jewelry = Market.builder().recentPrice(200).build();
            contentResource.put("3티어 1레벨 보석", tier3Jewelry);
            contentResource.put("4티어 1레벨 보석", tier4Jewelry);
            given(marketService.findLevelUpResource()).willReturn(contentResource);

            DayContent tier3Guardian = DayContent.builder()
                    .level(1540.0)
                    .category(Category.가디언토벌)
                    .jewelry(0.5)
                    .build();

            given(contentRepository.findAllByDayContent()).willReturn(List.of(tier3Guardian));

            // when
            dayTodoResetService.updateDayTodoGold();

            // then
            verify(marketService).findLevelUpResource();
            verify(contentRepository).findAllByDayContent();
            verify(characterRepository).updateDayContentPriceGuardian(any(DayContent.class), anyDouble());
        }

        @Test
        @DisplayName("성공 - 가디언 가격 업데이트 (4티어)")
        void success_tier4() {
            // given
            Map<String, Market> contentResource = new HashMap<>();
            Market tier3Jewelry = Market.builder().recentPrice(100).build();
            Market tier4Jewelry = Market.builder().recentPrice(200).build();
            contentResource.put("3티어 1레벨 보석", tier3Jewelry);
            contentResource.put("4티어 1레벨 보석", tier4Jewelry);
            given(marketService.findLevelUpResource()).willReturn(contentResource);

            DayContent tier4Guardian = DayContent.builder()
                    .level(1680.0)
                    .category(Category.가디언토벌)
                    .jewelry(0.5)
                    .build();

            given(contentRepository.findAllByDayContent()).willReturn(List.of(tier4Guardian));

            // when
            dayTodoResetService.updateDayTodoGold();

            // then
            verify(characterRepository).updateDayContentPriceGuardian(any(DayContent.class), anyDouble());
        }

        @Test
        @DisplayName("성공 - 카오스 던전은 필터링됨")
        void success_chaosDungeonFiltered() {
            // given
            Map<String, Market> contentResource = new HashMap<>();
            Market jewelry = Market.builder().recentPrice(100).build();
            contentResource.put("3티어 1레벨 보석", jewelry);
            contentResource.put("4티어 1레벨 보석", jewelry);
            given(marketService.findLevelUpResource()).willReturn(contentResource);

            DayContent chaosDungeon = DayContent.builder()
                    .level(1540.0)
                    .category(Category.카오스던전)
                    .jewelry(0.5)
                    .build();

            given(contentRepository.findAllByDayContent()).willReturn(List.of(chaosDungeon));

            // when
            dayTodoResetService.updateDayTodoGold();

            // then
            verify(characterRepository, times(0)).updateDayContentPriceGuardian(any(), anyDouble());
        }

        @Test
        @DisplayName("성공 - 여러 가디언 콘텐츠 처리")
        void success_multipleGuardians() {
            // given
            Map<String, Market> contentResource = new HashMap<>();
            Market tier3Jewelry = Market.builder().recentPrice(100).build();
            Market tier4Jewelry = Market.builder().recentPrice(200).build();
            contentResource.put("3티어 1레벨 보석", tier3Jewelry);
            contentResource.put("4티어 1레벨 보석", tier4Jewelry);
            given(marketService.findLevelUpResource()).willReturn(contentResource);

            DayContent guardian1 = DayContent.builder()
                    .level(1540.0)
                    .category(Category.가디언토벌)
                    .jewelry(0.5)
                    .build();
            DayContent guardian2 = DayContent.builder()
                    .level(1680.0)
                    .category(Category.가디언토벌)
                    .jewelry(0.6)
                    .build();

            given(contentRepository.findAllByDayContent()).willReturn(List.of(guardian1, guardian2));

            // when
            dayTodoResetService.updateDayTodoGold();

            // then
            verify(characterRepository, times(2)).updateDayContentPriceGuardian(any(DayContent.class), anyDouble());
        }
    }

    @Nested
    @DisplayName("updateCustomDailyTodo 메서드")
    class UpdateCustomDailyTodoTest {

        @Test
        @DisplayName("성공 - 커스텀 일일 숙제 초기화")
        void success() {
            // given
            given(customTodoRepository.update(CustomTodoFrequencyEnum.DAILY)).willReturn(50L);

            // when
            long result = dayTodoResetService.updateCustomDailyTodo();

            // then
            assertThat(result).isEqualTo(50L);
            verify(customTodoRepository).update(CustomTodoFrequencyEnum.DAILY);
        }

        @Test
        @DisplayName("성공 - 초기화할 커스텀 숙제가 없는 경우")
        void success_noCustomTodos() {
            // given
            given(customTodoRepository.update(CustomTodoFrequencyEnum.DAILY)).willReturn(0L);

            // when
            long result = dayTodoResetService.updateCustomDailyTodo();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("resetServerTodoState 메서드")
    class ResetServerTodoStateTest {

        @Test
        @DisplayName("성공 - 서버 숙제 상태 초기화")
        void success() {
            // given
            given(serverTodoStateRepository.resetAllChecked()).willReturn(30L);

            // when
            long result = dayTodoResetService.resetServerTodoState();

            // then
            assertThat(result).isEqualTo(30L);
            verify(serverTodoStateRepository).resetAllChecked();
        }

        @Test
        @DisplayName("성공 - 초기화할 상태가 없는 경우")
        void success_noStates() {
            // given
            given(serverTodoStateRepository.resetAllChecked()).willReturn(0L);

            // when
            long result = dayTodoResetService.resetServerTodoState();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }
}
