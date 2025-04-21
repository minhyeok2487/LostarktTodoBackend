package lostark.todo.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.RaidBusGoldRepository;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.schedule.dto.AuctionRequestDto;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
import lostark.todo.global.keyvalue.KeyValueRepository;
import lostark.todo.domain.market.enums.CategoryCode;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.lostark.client.LostarkMarketApiClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {
    private final LostarkMarketApiClient lostarkMarketApiClient;
    private final MarketService marketService;
    private final TodoV2Repository todoV2Repository;
    private final CharacterRepository characterRepository;
    private final CustomTodoRepository customTodoRepository;
    private final ContentRepository contentRepository;
    private final KeyValueRepository keyValueRepository;
    private final RaidBusGoldRepository raidBusGoldRepository;
    private final ScheduleRepository scheduleRepository;

    @Value("${Lostark-API-Key}")
    String apiKey;

    @Value("${API-KEY3}")
    public String apiKey3;

    // 매일 오전 0시 거래소 데이터 갱신
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetDayTodo() {
        log.info("휴식게이지 업데이트 = {}", characterRepository.updateDayContentGauge());
        log.info("이전 휴식게이지 저장 = {}", characterRepository.saveBeforeGauge());
        log.info("일일 숙제 초기화 = {}", characterRepository.updateDayContentCheck());

        log.info("일일 숙제 수익 계산");
        updateDayTodoGold();

        log.info("커스텀 일일 숙제 업데이트 = {}", customTodoRepository.update(CustomTodoFrequencyEnum.DAILY));
    }

    private void updateDayTodoGold() {
        Map<String, Market> contentResource = marketService.findLevelUpResource();

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
        Market jewelry = getJewelry(dayContent.getLevel(), contentResource);
        Market destruction = getMarketItem(dayContent.getLevel(), contentResource, "파괴석 결정", "파괴강석", "정제된 파괴강석", "운명의 파괴석");
        Market guardian = getMarketItem(dayContent.getLevel(), contentResource, "수호석 결정", "수호강석", "정제된 수호강석", "운명의 수호석");

        double price = destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += jewelry.getRecentPrice() * dayContent.getJewelry();
        return Math.round(price * 100.0) / 100.0;
    }

    private double calculateGuardianPrice(DayContent dayContent, Map<String, Market> contentResource) {
        Market destruction = getMarketItem(dayContent.getLevel(), contentResource, "파괴석 결정", "파괴강석", "정제된 파괴강석", "운명의 파괴석");
        Market guardian = getMarketItem(dayContent.getLevel(), contentResource, "수호석 결정", "수호강석", "정제된 수호강석", "운명의 수호석");
        Market leapStone = getMarketItem(dayContent.getLevel(), contentResource, "위대한 명예의 돌파석", "경이로운 명예의 돌파석", "찬란한 명예의 돌파석", "운명의 돌파석");

        double price = destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += leapStone.getRecentPrice() * dayContent.getLeapStone() / leapStone.getBundleCount();

        return Math.round(price * 100.0) / 100.0;
    }

    private Market getMarketItem(double itemLevel, Map<String, Market> contentResource,
                                 String level1Item, String level2Item, String level3Item, String level4Item) {
        if (itemLevel >= 1415 && itemLevel < 1490) {
            return contentResource.get(level1Item);
        } else if (itemLevel >= 1490 && itemLevel < 1580) {
            return contentResource.get(level2Item);
        } else if (itemLevel >= 1580 && itemLevel < 1640) {
            return contentResource.get(level3Item);
        } else {
            return contentResource.get(level4Item);
        }
    }

    private Market getJewelry(double itemLevel, Map<String, Market> contentResource) {
        if (itemLevel >= 1415 && itemLevel < 1640) {
            return contentResource.get("3티어 1레벨 보석");
        } else {
            return contentResource.get("4티어 1레벨 보석");
        }
    }


    /**
     * 수요일 오전 6시 2분 주간 숙제 초기화
     */
    @Scheduled(cron = "0 2 6 * * 3", zone = "Asia/Seoul")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetWeekTodo() {
        log.info("2주기 체크 값 변경 = {}", keyValueRepository.updateTwoCycle());

        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리

        todoV2Repository.resetTodoV2();
        log.info("todoV2Repository.resetTodoV2()"); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화

        // 일일 수익 주간 합 초기화
        characterRepository.updateWeekDayTodoTotalGold();

        log.info("커스텀 주간 숙제 업데이트 = {}", customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY));

        // 버스비 삭제
        raidBusGoldRepository.deleteAllRaidBusGold();
    }

    // 매일 10분마다 일정 레이드 자동 체크
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkScheduleRaids() {
        scheduleRepository.checkScheduleRaids();
    }
}
