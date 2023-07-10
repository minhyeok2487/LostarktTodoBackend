package lostark.todo.service;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SchedulerServiceTest {

    @Autowired CharacterRepository characterRepository;

    @Test
    public void calculateDayContentGauge() {
        // 휴식게이지 계산
        for (Character character : characterRepository.findAll()) {
            if(character.getChaos() != 0) {
                int chaos = character.getChaos();
                for(int i=0; i<chaos; i++) {
                    int subtract = subtract(character.getChaosGauge(), 20);
                    character.calculateChaos(subtract);
                }
            }
            if(character.getGuardian() != 0) {
                int guardian = character.getGuardian();
                for (int i = 0; i < guardian; i++) {
                    int subtract = subtract(character.getGuardianGauge(), 20);
                    character.calculateGuardian(subtract);
                }
            }
            System.out.println("character = " + character.toString());
        }

    }

    // 휴식게이지 빼기시 음수가 되면 0을 리턴하는 메서드
    public int subtract(int a, int b) {
        int result = a - b;
        if (result < 0) {
            result = 0;
        }
        return result;
    }
}