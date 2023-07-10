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
