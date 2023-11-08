package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.todoV2.TodoV2Repository;
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
    private final TodoV2Repository todoV2Repository;
    private final CharacterRepository characterRepository;

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
            character.getDayTodo().calculateEpona(); // 에포나의뢰, 출석체크 초기화
            character.getDayTodo().calculateChaos(); // 카오스던전 휴식게이지 계산 후 초기화
            character.getDayTodo().calculateGuardian(); // 가디언토벌 휴식게이지 계산 후 초기화
            // 반영된 휴식게이지로 일일숙제 예상 수익 계산
            characterService.calculateDayTodo(character, contentResource);
        });

        long endTime = System.currentTimeMillis(); // 작업 종료 시간 기록
        long executionTime = endTime - startTime; // 작업에 걸린 시간 계산

        log.info("reset day content. time: {} ms", executionTime);
    }


    /**
     * 수요일 오전 6시 주간 숙제 초기화
     */
    @Scheduled(cron = "0 0 6 * * 3")
    public void resetWeekTodo() {
        log.info("updateTodoV2 = {}", todoV2Repository.resetTodoV2CoolTime2()); // 2주기 레이드 처리

        log.info("todoV2Repository.resetTodoV2() = {}", todoV2Repository.resetTodoV2()); // 주간 레이드 초기화

        log.info("updateWeekContent = {}", characterRepository.updateWeekContent()); // 주간 숙제 초기화
    }
}
