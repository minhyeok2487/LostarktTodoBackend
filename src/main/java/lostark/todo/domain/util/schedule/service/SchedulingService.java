package lostark.todo.domain.util.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.util.content.repository.ContentRepository;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.util.schedule.dto.AuctionRequestDto;
import lostark.todo.global.keyvalue.KeyValueRepository;
import lostark.todo.domain.util.market.enums.CategoryCode;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.util.market.service.MarketService;
import lostark.todo.domain.lostark.client.LostarkMarketApiClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SchedulingService {
    private final LostarkMarketApiClient lostarkMarketApiClient;
    private final MarketService marketService;
    private final TodoV2Repository todoV2Repository;
    private final CharacterRepository characterRepository;
    private final CustomTodoRepository customTodoRepository;
    private final ContentRepository contentRepository;
    private final ScheduleService scheduleService;
    private final KeyValueRepository keyValueRepository;

    @Value("${Lostark-API-Key}")
    String apiKey;

    @Value("${API-KEY3}")
    public String apiKey3;

    // 매일 오전 0시 거래소 데이터 갱신
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateMarketData() {
        updateMarketItems();
        updateAuctionItems();
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
    public void resetDayTodo() {
        log.info("휴식게이지 업데이트 = {}",characterRepository.updateDayContentGauge());
        log.info("이전 휴식게이지 저장 = {}",characterRepository.saveBeforeGauge());
        log.info("일일 숙제 초기화 = {}",characterRepository.updateDayContentCheck());

        log.info("일일 숙제 수익 계산");
        updateDayTodoGold();

        log.info("커스텀 일일 숙제 업데이트 = {}", customTodoRepository.update(CustomTodoFrequencyEnum.DAILY));
    }

    public void updateDayTodoGold() {
        Map<String, Market> contentResource = marketService.findContentResource();

        List<DayContent> allByDayContent = contentRepository.findAllByDayContent();
        Map<DayContent, Double> chaosPrices = new HashMap<>();
        Map<DayContent, Double> guardianPrices = new HashMap<>();

        allByDayContent.forEach(dayContent -> {
            double price;
            switch (dayContent.getCategory()) {
                case 카오스던전:
                    price = calculateChaosPrice(dayContent, contentResource);
                    chaosPrices.put(dayContent, price);
                    break;
                case 가디언토벌:
                    price = calculateGuardianPrice(dayContent, contentResource);
                    guardianPrices.put(dayContent, price);
                    break;
            }
        });

        chaosPrices.forEach(characterRepository::updateDayContentPriceChaos);
        guardianPrices.forEach(characterRepository::updateDayContentPriceGuardian);
    }

    private double calculateChaosPrice(DayContent dayContent, Map<String, Market> contentResource) {
        int jewelryPrice = dayContent.getLevel() >= 1640 ? contentResource.get("4티어 1레벨 보석").getRecentPrice() : contentResource.get("3티어 1레벨 보석").getRecentPrice();
        double destructionStonePrice = getDestructionStonePrice(dayContent.getLevel(), contentResource);
        double guardianStonePrice = getGuardianStonePrice(dayContent.getLevel(), contentResource);
        double price = jewelryPrice * dayContent.getJewelry()
                + destructionStonePrice * dayContent.getDestructionStone() / 10
                + guardianStonePrice * dayContent.getGuardianStone() / 10;
        return Math.round(price * 100.0) / 100.0;
    }

    private double calculateGuardianPrice(DayContent dayContent, Map<String, Market> contentResource) {
        double destructionStonePrice = getDestructionStonePrice(dayContent.getLevel(), contentResource);
        double guardianStonePrice = getGuardianStonePrice(dayContent.getLevel(), contentResource);
        double leapStonePrice = getLeapStonePrice(dayContent.getLevel(), contentResource);

        double price = destructionStonePrice * dayContent.getDestructionStone() / 10
                + guardianStonePrice * dayContent.getGuardianStone() / 10
                + leapStonePrice * dayContent.getLeapStone();
        return Math.round(price * 100.0) / 100.0;
    }

    private double getDestructionStonePrice(double level, Map<String, Market> contentResource) {
        if (level >= 1640) {
            return contentResource.get("운명의 파괴석").getRecentPrice();
        } else if (level >= 1580) {
            return contentResource.get("정제된 파괴강석").getRecentPrice();
        } else if (level >= 1490) {
            return contentResource.get("파괴강석").getRecentPrice();
        } else {
            return contentResource.get("파괴석 결정").getRecentPrice();
        }
    }

    private double getGuardianStonePrice(double level, Map<String, Market> contentResource) {
        if (level >= 1640) {
            return contentResource.get("운명의 수호석").getRecentPrice();
        } else if (level >= 1580) {
            return contentResource.get("정제된 수호강석").getRecentPrice();
        } else if (level >= 1490) {
            return contentResource.get("수호강석").getRecentPrice();
        } else {
            return contentResource.get("수호석 결정").getRecentPrice();
        }
    }

    private double getLeapStonePrice(double level, Map<String, Market> contentResource) {
        if (level >= 1640) {
            return contentResource.get("운명의 돌파석").getRecentPrice();
        } else if (level >= 1580) {
            return contentResource.get("찬란한 명예의 돌파석").getRecentPrice();
        } else if (level >= 1490) {
            return contentResource.get("경이로운 명예의 돌파석").getRecentPrice();
        } else {
            return contentResource.get("위대한 명예의 돌파석").getRecentPrice();
        }
    }


    /**
     * 수요일 오전 6시 1분 주간 숙제 초기화
     */
    @Scheduled(cron = "0 1 6 * * 3", zone = "Asia/Seoul")
    public void resetWeekTodo() {
        log.info("2주기 체크 값 변경 = {}", keyValueRepository.updateTwoCycle());

        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리

        todoV2Repository.resetTodoV2();
        log.info("todoV2Repository.resetTodoV2()"); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화

        // 일일 수익 주간 합 초기화
        characterRepository.updateWeekDayTodoTotalGold();

        log.info("커스텀 주간 숙제 업데이트 = {}", customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY));
    }

    // 10분 마다 해당 시간 이전 일정 체크
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    public void checkSchedule() {
        log.info("스케줄 일정 체크 = {}", scheduleService.checkSchedule());
    }
}
