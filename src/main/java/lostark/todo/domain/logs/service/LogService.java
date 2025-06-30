package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.dto.TodoResponseDto;
import lostark.todo.domain.character.dto.UpdateWeekRaidCheckRequest;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
import lostark.todo.domain.logs.dto.*;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.repository.LogsRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogsRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Logs logs) {
        try {
            Optional<Logs> existingLog = repository.get(logs.getCharacterId(), logs.getLogContent(), logs.getLocalDate());
            if (existingLog.isPresent()) {
                Logs logToUpdate = existingLog.get();
                logToUpdate.setDeleted(logs.isDeleted());
                logToUpdate.setMessage(logs.getMessage());
                logToUpdate.setProfit(logs.getProfit());
                repository.save(logToUpdate);
            } else {
                repository.save(logs);
            }
        } catch (DataAccessException e) {
            log.error("로그 처리 실패: characterId={}, logContent={}", logs.getCharacterId(), logs.getLogContent(), e);
            throw e;
        }
    }

    // 주간 숙제 수익 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processWeekLog(UpdateWeekRaidCheckRequest request, CharacterResponse response) {
        response.getTodoList().stream()
                .filter(todo -> todo.getWeekCategory().equals(request.getWeekCategory()))
                .forEach(todo -> {
                    int gold = (response.isGoldCharacter() && todo.isGoldCheck()) ? todo.getRealGold() : 0;
                    String message = formatRaidLogMessage(todo, response, gold);
                    saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID, todo.getWeekCategory(), todo.isCheck(), message, gold);

//                    // 취소하면 더보기도 초기화
//                    if (!todo.isCheck()) {
//                        logService.deleteMoreRewardLogs(response.getMemberId(), response.getCharacterId(), request.getWeekCategory());
//                    }

                });
    }

    private String formatRaidLogMessage(TodoResponseDto todo, CharacterResponse response, int gold) {
        StringBuilder message = new StringBuilder(response.getServerName() + " 서버의 " +
                response.getCharacterName() + "(" + response.getItemLevel() + ")" + " 캐릭터가 ");
        message.append(todo.getName()).append("를 클리어해서 ");
        message.append(gold).append("골드를 획득했습니다.");
        if (!(response.isGoldCharacter() && todo.isGoldCheck())) {
            message.append(" (골드 미회득 레이드)");
        }
        return sanitizeMessage(message.toString());
    }

    private String sanitizeMessage(String message) {
        return message.replace("<br />", "").replace("</br>", "").replace("<br>", "");
    }

    // 일일 숙제 수익 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processDayLog(DayTodoCategoryEnum category, CharacterResponse response) {
        LogType logType = LogType.DAILY;
        StringBuilder message = new StringBuilder(response.getServerName() + " 서버의 " +
                response.getCharacterName() + "(" + response.getItemLevel() + ")" + " 캐릭터가 ");
        String name;
        double profit;

        switch (category) {
            case chaos:
                name = (response.getItemLevel() >= 1640) ? "쿠르잔전선" : "카오스던전";
                message.append(name).append("에서 ");
                profit = response.getChaosGold();
                message.append(profit).append("골드를 획득했습니다.");
                saveCharacterResponseLog(response, logType, LogContent.CHAOS, name,
                        response.getChaosCheck() == 2, message.toString(), profit);
                break;
            case guardian:
                name = "가디언토벌";
                message.append(response.getGuardian().getName());
                message.append("(").append(name).append(")").append("에서 ");
                profit = response.getGuardianGold();
                message.append(profit).append("골드를 획득했습니다.");
                saveCharacterResponseLog(response, logType, LogContent.GUARDIAN, name,
                        response.getGuardianCheck() == 1, message.toString(), profit);
                break;
            default:
        }
    }

    private void saveCharacterResponseLog(CharacterResponse response, LogType logType, LogContent content, String name,
                                          boolean shouldSave, String message, double profit) {
        LocalDate logDate = getLocalDate();

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(response.getMemberId())
                .characterId(response.getCharacterId())
                .logType(logType)
                .logContent(content)
                .name(name)
                .message(shouldSave ? message : message + " (삭제)")
                .profit(profit)
                .deleted(!shouldSave) // shouldSave == false면 deleted = true
                .build();

        eventPublisher.publishEvent(new LogCreatedEvent(logs));
    }

    private static LocalDate getLocalDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetTime = now.toLocalDate().atStartOfDay().plusHours(6);
        return now.isBefore(resetTime) ? now.toLocalDate().minusDays(1) : now.toLocalDate();
    }
}