package lostark.todo.domain.inspection.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.inspection.entity.InspectionCharacter;
import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionScheduleService {

    private final InspectionCharacterRepository inspectionCharacterRepository;
    private final InspectionService inspectionService;

    /**
     * 매 정시에 실행되어 해당 시간에 수집 설정된 사용자의 캐릭터 데이터를 수집
     * 캐릭터별로 개별 트랜잭션으로 처리 (InspectionService.fetchDailyData의 @Transactional 활용)
     */
    @Scheduled(cron = "0 2 * * * ?", zone = "Asia/Seoul")
    @SchedulerLock(name = "fetchScheduledInspectionData", lockAtMostFor = "55m", lockAtLeastFor = "1m")
    public void fetchScheduledInspectionData() {
        int currentHour = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).getHour();
        log.info("===== 군장검사 스케줄러 실행 ({}시) =====", currentHour);

        List<InspectionCharacter> characters = inspectionCharacterRepository
                .findActiveByScheduleHour(currentHour);

        if (characters.isEmpty()) {
            log.info("{}시에 수집할 캐릭터가 없습니다.", currentHour);
            return;
        }

        log.info("{}시 수집 대상 캐릭터 수: {}", currentHour, characters.size());

        processCharacters(characters);
    }

    private void processCharacters(List<InspectionCharacter> characters) {
        int successCount = 0;
        int failCount = 0;

        for (InspectionCharacter character : characters) {
            try {
                String apiKey = character.getMember().getApiKey();
                if (apiKey == null || apiKey.isEmpty()) {
                    log.warn("API 키 없음 - 캐릭터: {}, 멤버: {}",
                            character.getCharacterName(), character.getMember().getUsername());
                    failCount++;
                    continue;
                }

                // 캐릭터별 개별 트랜잭션으로 처리
                inspectionService.fetchDailyData(character, apiKey);
                successCount++;

                // API rate limit 방지 (200ms 간격)
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("스케줄러 인터럽트 발생");
                break;
            } catch (Exception e) {
                failCount++;
                log.error("데이터 수집 실패 - 캐릭터: {}",
                        character.getCharacterName(), e);
            }
        }

        log.info("===== 군장검사 스케줄러 완료 - 성공: {}, 실패: {} =====", successCount, failCount);
    }
}
