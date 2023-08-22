package lostark.todo.service;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Rollback(value = false)
class SchedulerServiceTest {

    @Autowired
    CharacterService characterService;

    @Test
    void calculateDayContentGauge() {
        // 휴식게이지 계산
        for (Character character : characterService.findAll()) {
            int chaosResult = getChaosResult(character);
            character.getCharacterDayContent().calculateChaos(chaosResult);

            int guardianResult = getGuardianResult(character);
            character.getCharacterDayContent().calculateGuardian(guardianResult);
        }
    }

    private int getGuardianResult(Character character) {
        int guardianResult = 0;
        int guardian = character.getCharacterDayContent().getGuardianCheck();
        int guardianGauge = character.getCharacterDayContent().getGuardianGauge();
        if(guardian == 0) {
            guardianResult = add(guardianGauge, 20);
        }
        if(guardian == 1) {
            guardianResult = subtract(guardianGauge, 10);
        }
        return guardianResult;
    }

    private int getChaosResult(Character character) {
        int chaosResult = 0;
        int chaos = character.getCharacterDayContent().getChaosCheck();
        int chaosGauge = character.getCharacterDayContent().getChaosGauge();
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