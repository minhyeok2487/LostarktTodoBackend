package lostark.todo.domain.logs.customAnnotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.dto.UpdateDayCheckRequest;
import lostark.todo.domain.character.dto.UpdateWeekRaidCheckRequest;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogService service;

    @AfterReturning(pointcut = "@annotation(loggable)", returning = "resultObject")
    public void characterResponseLogs(JoinPoint joinPoint, Object resultObject, Loggable loggable) {
        if (!(resultObject instanceof ResponseEntity<?> responseEntity)) {
            log.warn("Unexpected return type: {}", resultObject.getClass().getName());
            return;
        }

        if (!(responseEntity.getBody() instanceof CharacterResponse response)) {
            log.warn("Unexpected response body type: {}", Objects.requireNonNull(responseEntity.getBody()).getClass().getName());
            return;
        }

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UpdateDayCheckRequest request) {
                processDayLog(request, response);
            } else if (arg instanceof UpdateWeekRaidCheckRequest request) {
                processWeekLog(request, response);
            }
        }
    }

    private void processWeekLog(UpdateWeekRaidCheckRequest request, CharacterResponse response) {
        response.getTodoList().stream()
                .filter(todo -> todo.getWeekCategory().equals(request.getWeekCategory()))
                .forEach(todo -> {
                    int gold = (response.isGoldCharacter() && todo.isGoldCheck()) ? todo.getGold() : 0;
                    String message = formatRaidLogMessage(todo, response);
                    saveOrDeleteLog(response, LogType.WEEKLY, LogContent.RAID, todo.isCheck(), message, gold);
                });
    }

    private void processDayLog(UpdateDayCheckRequest request, CharacterResponse response) {
        LogType logType = LogType.DAILY;
        String message;
        double profit;

        switch (request.getCategory()) {
            case chaos:
                message = (response.getItemLevel() >= 1640) ? "쿠르잔전선 클리어" : "카오스던전 클리어";
                profit = response.getChaosGold();
                saveOrDeleteLog(response, logType, LogContent.CHAOS, response.getChaosCheck() == 2, message, profit);
                break;
            case guardian:
                message = "가디언토벌 클리어";
                profit = response.getGuardianGold();
                saveOrDeleteLog(response, logType, LogContent.GUARDIAN, response.getGuardianCheck() == 1, message, profit);
                break;
            default:
                log.warn("Unsupported DayTodoCategoryEnum: {}", request.getCategory());
        }
    }

    private String formatRaidLogMessage(TodoResponseDto todo, CharacterResponse response) {
        String message = todo.getName() + " 클리어";
        if (!(response.isGoldCharacter() && todo.isGoldCheck())) {
            message += " (골드 미회득)";
        }
        return sanitizeMessage(message);
    }

    private String sanitizeMessage(String message) {
        return message.replace("<br />", "").replace("</br>", "").replace("<br>", "");
    }

    private void saveOrDeleteLog(CharacterResponse result, LogType logType, LogContent content, boolean shouldSave, String message, double profit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetTime = now.toLocalDate().atStartOfDay().plusHours(6);
        LocalDate logDate = now.isBefore(resetTime) ? now.toLocalDate().minusDays(1) : now.toLocalDate();

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(result.getMemberId())
                .characterId(result.getCharacterId())
                .logType(logType)
                .logContent(content)
                .message(message)
                .profit(profit)
                .build();

        if (shouldSave) {
            service.saveLog(logs);
        } else {
            service.deleteLog(logs);
        }
    }
}
