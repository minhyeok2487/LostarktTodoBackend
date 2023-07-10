package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulerService {

    private final CharacterRepository characterRepository;

    /**
     * 매일 오전 6시 일일 숙제 초기화
     */
    @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시에 실행
    public void calculateDayContentGauge() {
        // 휴식게이지 계산
        for (Character character : characterRepository.findAll()) {
            int chaosResult = getChaosResult(character);
            character.calculateChaos(chaosResult);

            int guardianResult = getGuardianResult(character);
            character.calculateGuardian(guardianResult);
        }
    }

    private int getGuardianResult(Character character) {
        int guardianResult = 0;
        int guardian = character.getGuardian();
        int guardianGauge = character.getGuardianGauge();
        if(guardian == 0) {
            guardianResult = add(guardianGauge, 20);
        }
        if(guardian == 1) {
            guardianResult = subtract(guardianGauge, 10);
        }
        if(guardian == 2) {
            guardianResult = subtract(guardianGauge, 40);
        }
        return guardianResult;
    }

    private int getChaosResult(Character character) {
        int chaosResult = 0;
        int chaos = character.getChaos();
        int chaosGauge = character.getChaosGauge();
        if(chaos == 0) {
            chaosResult = add(chaosGauge, 20);
        }
        if(chaos == 1) {
            chaosResult = subtract(chaosGauge, 10);
        }
        if(chaos == 2) {
            chaosResult = subtract(chaosGauge, 40);
        }
        return chaosResult;
    }

    // 휴식게이지 빼기시 음수가 되면 0을 리턴하는 메서드
    public int subtract(int a, int b) {
        int result = a - b;
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    // 휴식게이지 더하기시 100이 넘으면 100을 리턴하는 메서드
    public int add(int a, int b) {
        int result = a + b;
        if (result > 100) {
            result = 100;
        }
        return result;
    }

}
