package lostark.todo.domain.schedule.service;

import lostark.todo.domain.lostark.client.LostarkMarketApiClient;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.enums.CategoryCode;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.member.repository.LifeEnergyRepository;
import lostark.todo.domain.schedule.dto.AuctionRequestDto;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    @Mock
    private LostarkMarketApiClient lostarkMarketApiClient;

    @Mock
    private MarketService marketService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private LifeEnergyRepository lifeEnergyRepository;

    @Mock
    private DayTodoResetService dayTodoResetService;

    @Mock
    private WeekTodoResetService weekTodoResetService;

    @InjectMocks
    private SchedulingService schedulingService;

    @Nested
    @DisplayName("updateMarketData 메서드")
    class UpdateMarketDataTest {

        @Test
        @DisplayName("성공 - 거래소 데이터 갱신")
        void success() {
            // given
            ReflectionTestUtils.setField(schedulingService, "apiKey", "test-api-key");

            List<Market> marketList = List.of(
                    Market.builder().name("파괴석 결정").build(),
                    Market.builder().name("수호석 결정").build()
            );
            given(lostarkMarketApiClient.getMarketData(anyInt(), anyString()))
                    .willReturn(marketList);

            JSONObject auctionData = new JSONObject();
            given(lostarkMarketApiClient.getAuctionItems(any(AuctionRequestDto.class), anyString()))
                    .willReturn(auctionData);

            // when
            schedulingService.updateMarketData();

            // then
            verify(lostarkMarketApiClient).getMarketData(CategoryCode.재련재료.getValue(), "test-api-key");
            verify(marketService).updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
            verify(lostarkMarketApiClient, times(2)).getAuctionItems(any(AuctionRequestDto.class), anyString());
            verify(marketService).updateAuctionItemList(anyList());
        }

        @Test
        @DisplayName("성공 - 거래소 데이터가 비어있어도 정상 처리")
        void success_emptyMarketData() {
            // given
            ReflectionTestUtils.setField(schedulingService, "apiKey", "test-api-key");

            given(lostarkMarketApiClient.getMarketData(anyInt(), anyString()))
                    .willReturn(List.of());

            given(lostarkMarketApiClient.getAuctionItems(any(AuctionRequestDto.class), anyString()))
                    .willReturn(new JSONObject());

            // when
            schedulingService.updateMarketData();

            // then
            verify(marketService).updateMarketItemList(anyList(), anyInt());
            verify(marketService).updateAuctionItemList(anyList());
        }
    }

    @Nested
    @DisplayName("resetDayTodo 메서드")
    class ResetDayTodoTest {

        @Test
        @DisplayName("성공 - 모든 단계 정상 실행")
        void success_allSteps() {
            // given
            given(dayTodoResetService.updateDayContentGauge()).willReturn(100L);
            given(dayTodoResetService.saveBeforeGauge()).willReturn(100L);
            given(dayTodoResetService.updateDayContentCheck()).willReturn(100L);
            doNothing().when(dayTodoResetService).updateDayTodoGold();
            given(dayTodoResetService.updateCustomDailyTodo()).willReturn(50L);
            given(dayTodoResetService.resetServerTodoState()).willReturn(30L);

            // when
            schedulingService.resetDayTodo();

            // then
            verify(dayTodoResetService).updateDayContentGauge();
            verify(dayTodoResetService).saveBeforeGauge();
            verify(dayTodoResetService).updateDayContentCheck();
            verify(dayTodoResetService).updateDayTodoGold();
            verify(dayTodoResetService).updateCustomDailyTodo();
            verify(dayTodoResetService).resetServerTodoState();
        }

        @Test
        @DisplayName("성공 - 일부 단계 실패해도 다른 단계 계속 실행")
        void success_continuesOnPartialFailure() {
            // given
            given(dayTodoResetService.updateDayContentGauge()).willReturn(100L);
            given(dayTodoResetService.saveBeforeGauge()).willThrow(new RuntimeException("DB 오류"));
            given(dayTodoResetService.updateDayContentCheck()).willReturn(100L);
            doNothing().when(dayTodoResetService).updateDayTodoGold();
            given(dayTodoResetService.updateCustomDailyTodo()).willReturn(50L);
            given(dayTodoResetService.resetServerTodoState()).willReturn(30L);

            // when
            schedulingService.resetDayTodo();

            // then
            verify(dayTodoResetService).updateDayContentGauge();
            verify(dayTodoResetService).saveBeforeGauge();
            verify(dayTodoResetService).updateDayContentCheck();
            verify(dayTodoResetService).updateDayTodoGold();
            verify(dayTodoResetService).updateCustomDailyTodo();
            verify(dayTodoResetService).resetServerTodoState();
        }
    }

    @Nested
    @DisplayName("resetWeekTodo 메서드")
    class ResetWeekTodoTest {

        @Test
        @DisplayName("성공 - 모든 단계 정상 실행")
        void success_allSteps() {
            // given
            given(weekTodoResetService.updateTwoCycle()).willReturn(1);
            given(weekTodoResetService.resetTodoV2CoolTime2()).willReturn(50L);
            doNothing().when(weekTodoResetService).resetTodoV2();
            given(weekTodoResetService.updateWeekContent()).willReturn(100L);
            doNothing().when(weekTodoResetService).updateWeekDayTodoTotalGold();
            given(weekTodoResetService.updateCustomWeeklyTodo()).willReturn(30L);
            doNothing().when(weekTodoResetService).deleteAllRaidBusGold();

            // when
            schedulingService.resetWeekTodo();

            // then
            verify(weekTodoResetService).updateTwoCycle();
            verify(weekTodoResetService).resetTodoV2CoolTime2();
            verify(weekTodoResetService).resetTodoV2();
            verify(weekTodoResetService).updateWeekContent();
            verify(weekTodoResetService).updateWeekDayTodoTotalGold();
            verify(weekTodoResetService).updateCustomWeeklyTodo();
            verify(weekTodoResetService).deleteAllRaidBusGold();
        }

        @Test
        @DisplayName("성공 - 일부 단계 실패해도 다른 단계 계속 실행")
        void success_continuesOnPartialFailure() {
            // given
            given(weekTodoResetService.updateTwoCycle()).willReturn(1);
            given(weekTodoResetService.resetTodoV2CoolTime2()).willThrow(new RuntimeException("오류"));
            doNothing().when(weekTodoResetService).resetTodoV2();
            given(weekTodoResetService.updateWeekContent()).willReturn(100L);
            doNothing().when(weekTodoResetService).updateWeekDayTodoTotalGold();
            given(weekTodoResetService.updateCustomWeeklyTodo()).willReturn(30L);
            doNothing().when(weekTodoResetService).deleteAllRaidBusGold();

            // when
            schedulingService.resetWeekTodo();

            // then
            verify(weekTodoResetService).updateTwoCycle();
            verify(weekTodoResetService).resetTodoV2CoolTime2();
            verify(weekTodoResetService).resetTodoV2();
        }
    }

    @Nested
    @DisplayName("checkScheduleRaids 메서드")
    class CheckScheduleRaidsTest {

        @Test
        @DisplayName("성공 - 레이드 자동 체크")
        void success() {
            // when
            schedulingService.checkScheduleRaids();

            // then
            verify(scheduleRepository).checkScheduleRaids();
        }
    }

    @Nested
    @DisplayName("addEnergyToAllLifeEnergies 메서드")
    class AddEnergyToAllLifeEnergiesTest {

        @Test
        @DisplayName("성공 - 생활의 기운 추가")
        void success() {
            // given
            given(lifeEnergyRepository.addEnergyToAllLifeEnergies()).willReturn(50);

            // when
            schedulingService.addEnergyToAllLifeEnergies();

            // then
            verify(lifeEnergyRepository).addEnergyToAllLifeEnergies();
        }

        @Test
        @DisplayName("성공 - 추가할 생활의 기운이 없는 경우")
        void success_noEnergyToAdd() {
            // given
            given(lifeEnergyRepository.addEnergyToAllLifeEnergies()).willReturn(0);

            // when
            schedulingService.addEnergyToAllLifeEnergies();

            // then
            verify(lifeEnergyRepository).addEnergyToAllLifeEnergies();
        }
    }
}
