package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SchedulerService {

    private final CharacterService characterService;
    private final LostarkMarketService lostarkMarketService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final TodoService todoService;

    @Value("${Lostark-API-Key}")
    String apiKey;

    /**
     * 매일 오전 0시 거래소 데이터 갱신
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateMarketData() {
        List<Market> marketList = lostarkMarketService.getMarketData(CategoryCode.재련재료.getValue(), apiKey);
        marketService.updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
    }

    /**
     * 매일 오전 6시 일일 숙제 초기화
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void resetDayTodo() {
        long startTime = System.currentTimeMillis(); // 작업 시작 시간 기록

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 휴식게이지 계산
        characterService.findAll().forEach(character -> {
            character.getDayTodo().setEponaCheck(false); // 에포나의뢰, 출석체크 초기화
            character.getDayTodo().calculateChaos(); // 카오스던전 휴식게이지 계산 후 초기화
            character.getDayTodo().calculateGuardian(); // 가디언토벌 휴식게이지 계산 후 초기화
            // 반영된 휴식게이지로 일일숙제 예상 수익 계산
            characterService.calculateDayTodo(character, contentResource);
        });

        long endTime = System.currentTimeMillis(); // 작업 종료 시간 기록
        long executionTime = endTime - startTime; // 작업에 걸린 시간 계산

        log.info("일일 숙제 초기화 완료. 소요 시간: {} 밀리초", executionTime);
    }


    /**
     * 수요일 오전 6시 주간 숙제 초기화
     */
    @Scheduled(cron = "0 0 6 * * 3")
    public void resetWeekTodo() {
        todoService.findAll().forEach(todo -> {
            todo.setChecked(false);
        });
        characterService.findAll().forEach(character -> {
            character.setChallengeAbyss(false);
            character.setChallengeAbyss(false);
        });
        log.info("주간 숙제 초기화 완료");
    }
}
