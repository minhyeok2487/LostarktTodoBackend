//package lostark.todo.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import lostark.todo.domain.character.CharacterRepository;
//import lostark.todo.domain.content.Category;
//import lostark.todo.domain.content.ContentRepository;
//import lostark.todo.domain.content.DayContent;
//import lostark.todo.domain.market.CategoryCode;
//import lostark.todo.domain.market.Market;
//import lostark.todo.domain.notices.Notices;
//import lostark.todo.domain.todoV2.TodoV2Repository;
//import lostark.todo.service.discordWebHook.DiscordWebhook;
//import lostark.todo.service.lostarkApi.LostarkMarketService;
//import lostark.todo.service.lostarkApi.LostarkNewsService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.awt.*;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class SchedulerService {
//    private final LostarkMarketService lostarkMarketService;
//    private final MarketService marketService;
//    private final TodoV2Repository todoV2Repository;
//    private final CharacterRepository characterRepository;
//    private final ContentRepository contentRepository;
//    private final LostarkNewsService newsService;
//    private final NoticesService noticesService;
//    private final WebHookService webHookService;
//
//    @Value("${Lostark-API-Key}")
//    String apiKey;
//
//    @Value("${API-KEY3}")
//    public String apiKey3;
//
//    @Value("${discord.noticeURL}")
//    private String noticeUrl;
//
//    /**
//     * 매일 오전 0시 거래소 데이터 갱신
//     */
//    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
//    public void updateMarketData() {
//        List<Market> marketList = lostarkMarketService.getMarketData(CategoryCode.재련재료.getValue(), apiKey);
//        marketService.updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
//    }
//
//    /**
//     * 매일 오전 6시 일일 숙제 초기화
//     */
//    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Seoul")
//    public void resetDayTodo() {
//        log.info("updateDayContentGauge() = {}",characterRepository.updateDayContentGauge()); //휴식게이지 계산
//        log.info("updateDayContentCheck() = {}",characterRepository.updateDayContentCheck()); //체크 초기화
//
//        // 반영된 휴식게이지로 일일숙제 예상 수익 계산
//        Map<String, Market> contentResource = marketService.findContentResource();
//        double jewelry = (double) contentResource.get("1레벨").getRecentPrice();
//        double 파괴석_결정 = (double) contentResource.get("파괴석 결정").getRecentPrice() / 10;
//        double 수호석_결정 = (double) contentResource.get("수호석 결정").getRecentPrice() / 10;
//        double 파괴강석 = (double) contentResource.get("파괴강석").getRecentPrice() / 10;
//        double 수호강석 = (double) contentResource.get("수호강석").getRecentPrice() / 10;
//        double 정제된_파괴강석 = (double) contentResource.get("정제된 파괴강석").getRecentPrice() / 10;
//        double 정제된_수호강석 = (double) contentResource.get("정제된 수호강석").getRecentPrice() / 10;
//
//        List<DayContent> allByDayContent = contentRepository.findAllByDayContent();
//        Map<DayContent, Double> chaos = new HashMap<>();
//        Map<DayContent, Double> guardian = new HashMap<>();
//        for (DayContent dayContent : allByDayContent) {
//            if (dayContent.getLevel()>=1415 && dayContent.getLevel()<1490) {
//                if(dayContent.getCategory().equals(Category.카오스던전)) {
//                    double price = jewelry * dayContent.getJewelry() + 파괴석_결정 * dayContent.getDestructionStone() + 수호석_결정 * dayContent.getGuardianStone();
//                    price = Math.round(price * 100.0) / 100.0;
//                    chaos.put(dayContent, price);
//                }
//                if(dayContent.getCategory().equals(Category.가디언토벌)) {
//                    double price = dayContent.getJewelry() + 파괴석_결정 * dayContent.getDestructionStone() + 수호석_결정 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("위대한 명예의 돌파석").getRecentPrice();
//                    price = Math.round(price * 100.0) / 100.0;
//                    guardian.put(dayContent, price);
//                }
//            } else if (dayContent.getLevel()>=1490 && dayContent.getLevel()<1580) {
//                if(dayContent.getCategory().equals(Category.카오스던전)) {
//                    double price = jewelry * dayContent.getJewelry() + 파괴강석 * dayContent.getDestructionStone() + 수호강석 * dayContent.getGuardianStone();
//                    price = Math.round(price * 100.0) / 100.0;
//                    chaos.put(dayContent, price);
//                }
//                if(dayContent.getCategory().equals(Category.가디언토벌)) {
//                    double price = dayContent.getJewelry() + 파괴강석 * dayContent.getDestructionStone() + 수호강석 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("경이로운 명예의 돌파석").getRecentPrice();
//                    price = Math.round(price * 100.0) / 100.0;
//                    guardian.put(dayContent, price);
//                }
//            } else {
//                if(dayContent.getCategory().equals(Category.카오스던전)) {
//                    double price = jewelry * dayContent.getJewelry() + 정제된_파괴강석 * dayContent.getDestructionStone() + 정제된_수호강석 * dayContent.getGuardianStone();
//                    price = Math.round(price * 100.0) / 100.0;
//                    chaos.put(dayContent, price);
//                }
//                if(dayContent.getCategory().equals(Category.가디언토벌)) {
//                    double price = dayContent.getJewelry() + 정제된_파괴강석 * dayContent.getDestructionStone() + 정제된_수호강석 * dayContent.getGuardianStone() + dayContent.getLeapStone() * contentResource.get("찬란한 명예의 돌파석").getRecentPrice();
//                    price = Math.round(price * 100.0) / 100.0;
//                    guardian.put(dayContent, price);
//                }
//            }
//        }
//
//        chaos.forEach((key, value) -> characterRepository.updateDayContentPriceChaos(key, value)); //카오스던전 계산
//        guardian.forEach((key, value) -> characterRepository.updateDayContentPriceGuardian(key, value)); //가디언토벌 계산
//        // 휴식게이지 저장
//        characterRepository.updateDayTodoGauge();
//    }
//
//
//    /**
//     * 수요일 오전 6시 1분 주간 숙제 초기화
//     */
//    @Scheduled(cron = "0 1 6 * * 3", zone = "Asia/Seoul")
//    public void resetWeekTodo() {
//        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리
//
//        log.info("todoV2Repository.resetTodoV2() = {}", todoV2Repository.resetTodoV2()); // 주간 레이드 초기화
//
//        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화
//
//        // 일일 수익 주간 합 초기화
//        characterRepository.updateWeekDayTodoTotalGold();
//    }
//
//
//    /*로스트아크 새로운 공지사항 가져와서 저장 (매 정각에 자동 실행)*/
//    @Scheduled(cron = "10 0 * * * *", zone = "Asia/Seoul")
//    public void getLostarkNotice() {
//        List<Notices> noticesList = newsService.getNoticeList(apiKey3);
//        for (Notices notices : noticesList) {
//            if (noticesService.save(notices)) {
//                webHookService.sendMessage(new DiscordWebhook.EmbedObject()
//                        .setTitle("새로운 로스트아크 공지사항이 저장되었습니다.")
//                        .addField("제목", notices.getTitle(), true)
//                        .addField("링크", notices.getLink(), false)
//                        .setColor(Color.GREEN), noticeUrl);
//            }
//        }
//    }
//}
