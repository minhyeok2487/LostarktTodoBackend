package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.customTodo.CustomTodoFrequencyEnum;
import lostark.todo.domain.customTodo.CustomTodoRepository;
import lostark.todo.domain.keyvalue.KeyValueRepository;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.notices.Notices;
import lostark.todo.domain.todoV2.TodoV2Repository;
import lostark.todo.domainV2.util.market.service.MarketService;
import lostark.todo.service.discordWebHook.DiscordWebhook;
import lostark.todo.domainV2.lostark.dao.LostarkMarketDao;
import lostark.todo.domainV2.lostark.dao.LostarkNewsDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SchedulingService {
    private final LostarkMarketDao lostarkMarketDao;
    private final MarketService marketService;
    private final TodoV2Repository todoV2Repository;
    private final CharacterRepository characterRepository;
    private final CustomTodoRepository customTodoRepository;
    private final ContentRepository contentRepository;
    private final LostarkNewsDao newsService;
    private final NoticesService noticesService;
    private final WebHookService webHookService;
    private final ScheduleService scheduleService;
    private final KeyValueRepository keyValueRepository;

    @Value("${Lostark-API-Key}")
    String apiKey;

    @Value("${API-KEY3}")
    public String apiKey3;

    @Value("${discord.noticeURL}")
    private String noticeUrl;

    /**
     * 매일 오전 0시 거래소 데이터 갱신
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateMarketData() {
        List<Market> marketList = lostarkMarketDao.getMarketData(CategoryCode.재련재료.getValue(), apiKey);
        marketService.updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
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

        log.info("todoV2Repository.resetTodoV2() = {}", todoV2Repository.resetTodoV2()); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화

        // 일일 수익 주간 합 초기화
        characterRepository.updateWeekDayTodoTotalGold();

        log.info("커스텀 주간 숙제 업데이트 = {}", customTodoRepository.update(CustomTodoFrequencyEnum.WEEKLY));
    }


    /*로스트아크 새로운 공지사항 가져와서 저장 (매 정각에 자동 실행)*/
    @Scheduled(cron = "10 0 * * * *", zone = "Asia/Seoul")
    public void getLostarkNotice() {
        List<Notices> noticesList = newsService.getNoticeList(apiKey3);
        for (Notices notices : noticesList) {
            if (noticesService.save(notices)) {
                webHookService.sendMessage(new DiscordWebhook.EmbedObject()
                        .setTitle("새로운 로스트아크 공지사항이 저장되었습니다.")
                        .addField("제목", notices.getTitle(), true)
                        .addField("링크", notices.getLink(), false)
                        .setColor(Color.GREEN), noticeUrl);
            }
        }
    }

    // 10분 마다 해당 시간 이전 일정 체크
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    public void checkSchedule() {
        log.info("스케줄 일정 체크 = {}", scheduleService.checkSchedule());
    }
}
