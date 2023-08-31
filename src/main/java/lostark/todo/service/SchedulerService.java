package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.domain.market.Market;
import lostark.todo.service.lostarkApi.LostarkMarketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulerService {

    private final CharacterRepository characterRepository;
    private final LostarkMarketService lostarkMarketService;
    private final MarketService marketService;

    @Value("${Lostark-API-Key}")
    String apiKey;

    /**
     * 매일 오전 0시 거래소 데이터 갱신
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateMarketData() {
        List<Market> marketList = lostarkMarketService.getMarketData(CategoryCode.재련재료.getValue(), apiKey);
        marketService.updateMarketItemList(marketList, CategoryCode.재련재료.getValue());
    }

    /**
     * 매일 오전 6시 일일 숙제 초기화
     */
    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Seoul")
    public void calculateDayContentGauge() {
        // 휴식게이지 계산
        for (Character character : characterRepository.findAll()) {
            character.getDayTodo().setEponaCheck(false);

            int chaosResult = getChaosResult(character);
            character.getDayTodo().calculateChaos(chaosResult);

            int guardianResult = getGuardianResult(character);
            character.getDayTodo().calculateGuardian(guardianResult);
        }
    }


    private int getGuardianResult(Character character) {
        int guardianResult = 0;
        int guardian = character.getDayTodo().getGuardianCheck();
        int guardianGauge = character.getDayTodo().getGuardianGauge();
        if(guardian == 0) {
            guardianResult = add(guardianGauge, 10);
        }
        if(guardian == 1) {
            guardianResult = subtract(guardianGauge, 20);
        }
        return guardianResult;
    }

    private int getChaosResult(Character character) {
        int chaosResult = 0;
        int chaosCheck = character.getDayTodo().getChaosCheck();
        int chaosGauge = character.getDayTodo().getChaosGauge();
        if(chaosCheck == 0) {
            chaosResult = add(chaosGauge, 20);
        }
        if(chaosCheck == 1) {
            if (chaosGauge >= 20) {
                chaosResult = subtract(chaosGauge, 10);
            } else {
                chaosResult = add(chaosGauge, 10);
            }
        }
        if(chaosCheck == 2) {
            chaosResult = subtract(chaosGauge, 40);
        }
        return chaosResult;
    }

    // 두 숫자 더하기
    // 단, 음수가 되면 0을 리턴하는 메서드
    public int subtract(int a, int b) {
        int result = a - b;
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    // 두 숫자 빼기
    // 단, 100이 넘으면 100을 리턴하는 메서드
    public int add(int a, int b) {
        int result = a + b;
        if (result > 100) {
            result = 100;
        }
        return result;
    }

}
