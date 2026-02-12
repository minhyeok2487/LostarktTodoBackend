package lostark.todo.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.repository.LifeEnergyRepository;
import lostark.todo.domain.schedule.dto.AuctionRequestDto;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
import lostark.todo.domain.market.enums.CategoryCode;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.lostark.client.LostarkMarketApiClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {

    private final LostarkMarketApiClient lostarkMarketApiClient;
    private final MarketService marketService;
    private final ScheduleRepository scheduleRepository;
    private final LifeEnergyRepository lifeEnergyRepository;
    private final DayTodoResetService dayTodoResetService;
    private final WeekTodoResetService weekTodoResetService;

    @Value("${Lostark-API-Key}")
    String apiKey;

    @Value("${API-KEY3}")
    public String apiKey3;

    // 매일 오전 0시 거래소 데이터 갱신
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")
    @SchedulerLock(name = "updateMarketData", lockAtMostFor = "30m", lockAtLeastFor = "1m")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateMarketData() {
        updateMarketItems();
        updateAuctionItems();
        log.info("거래소 데이터 갱신을 완료 하였습니다.");
    }

    private void updateMarketItems() {
        List<Market> marketList = lostarkMarketApiClient.getMarketData(CategoryCode.재련재료.getValue(), apiKey);
        marketService.updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
    }

    private void updateAuctionItems() {
        List<AuctionRequestDto> auctionRequests = List.of(
                new AuctionRequestDto(3, CategoryCode.보석.getValue(), "5레벨"),
                new AuctionRequestDto(4, CategoryCode.보석.getValue(), "5레벨")
        );

        List<JSONObject> auctionData = auctionRequests.stream()
                .map(request -> lostarkMarketApiClient.getAuctionItems(request, apiKey))
                .collect(Collectors.toList());

        marketService.updateAuctionItemList(auctionData);
    }

    // 매일 오전 6시 일일 숙제 초기화
    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Seoul")
    @SchedulerLock(name = "resetDayTodo", lockAtMostFor = "30m", lockAtLeastFor = "1m")
    public void resetDayTodo() {
        log.info("===== 일일 숙제 초기화 시작 =====");
        safeExecute(() -> dayTodoResetService.updateDayContentGauge(), "휴식게이지 업데이트");
        safeExecute(() -> dayTodoResetService.saveBeforeGauge(), "이전 휴식게이지 저장");
        safeExecute(() -> dayTodoResetService.updateDayContentCheck(), "일일 숙제 초기화");
        safeExecute(() -> { dayTodoResetService.updateDayTodoGold(); return null; }, "일일 숙제 수익 계산");
        safeExecute(() -> dayTodoResetService.updateCustomDailyTodo(), "커스텀 일일 숙제 업데이트");
        safeExecute(() -> dayTodoResetService.resetServerTodoState(), "서버 공통 숙제 체크 초기화");
        log.info("===== 일일 숙제 초기화 완료 =====");
    }


    /**
     * 수요일 오전 6시 2분 주간 숙제 초기화
     */
    @Scheduled(cron = "0 2 6 * * 3", zone = "Asia/Seoul")
    @SchedulerLock(name = "resetWeekTodo", lockAtMostFor = "30m", lockAtLeastFor = "1m")
    public void resetWeekTodo() {
        log.info("===== 주간 숙제 초기화 시작 =====");
        safeExecute(() -> weekTodoResetService.updateTwoCycle(), "2주기 체크 값 변경");
        safeExecute(() -> weekTodoResetService.resetTodoV2CoolTime2(), "2주기 레이드 처리");
        safeExecute(() -> { weekTodoResetService.resetTodoV2(); return null; }, "주간 레이드 초기화");
        safeExecute(() -> weekTodoResetService.updateWeekContent(), "주간 숙제 초기화");
        safeExecute(() -> { weekTodoResetService.updateWeekDayTodoTotalGold(); return null; }, "일일 수익 주간 합 초기화");
        safeExecute(() -> weekTodoResetService.updateCustomWeeklyTodo(), "커스텀 주간 숙제 업데이트");
        safeExecute(() -> { weekTodoResetService.deleteAllRaidBusGold(); return null; }, "버스비 삭제");
        log.info("===== 주간 숙제 초기화 완료 =====");
    }

    // 매일 10분마다 일정 레이드 자동 체크
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "checkScheduleRaids", lockAtMostFor = "9m", lockAtLeastFor = "1m")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkScheduleRaids() {
        scheduleRepository.checkScheduleRaids();
    }

    // 매일 5분, 35분마다 추가 (정시 스케줄 경합 방지를 위해 5분 오프셋)
    @Scheduled(cron = "0 5,35 * * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "addEnergyToAllLifeEnergies", lockAtMostFor = "29m", lockAtLeastFor = "1m")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addEnergyToAllLifeEnergies() {
        int count = lifeEnergyRepository.addEnergyToAllLifeEnergies();
        log.info("생활의 기운 업데이트: " + count + "개");
    }

    private void safeExecute(Supplier<?> task, String name) {
        try {
            Object result = task.get();
            if (result != null) {
                log.info("{} = {}", name, result);
            } else {
                log.info("{} 완료", name);
            }
        } catch (Exception e) {
            log.error("{} 실패: {}", name, e.getMessage(), e);
        }
    }
}
