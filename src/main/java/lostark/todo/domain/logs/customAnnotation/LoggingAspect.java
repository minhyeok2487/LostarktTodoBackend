package lostark.todo.domain.logs.customAnnotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.dto.UpdateDayCheckRequest;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
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
import java.util.Objects;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogService service;

    @AfterReturning(pointcut = "@annotation(loggable)", returning = "resultObject")
    public void characterResponseLogs(JoinPoint joinPoint, Object resultObject, Loggable loggable) {
        String methodName = joinPoint.getSignature().getName(); // 호출된 메서드명 추출

        if (!(resultObject instanceof ResponseEntity<?> responseEntity)) {
            log.warn("Unexpected return type: {}", resultObject.getClass().getName());
            return;
        }

        if (!(responseEntity.getBody() instanceof CharacterResponse result)) {
            log.warn("Unexpected response body type: {}", Objects.requireNonNull(responseEntity.getBody()).getClass().getName());
            return;
        }

        for (Object arg : joinPoint.getArgs()) {
            // 메서드 이름을 기준으로 조건 처리
            if (methodName.equals("updateDayCheck")) {
                if (arg instanceof UpdateDayCheckRequest request) {
                    processLog(request, result);
                }
            }
        }
    }

    private void processLog(UpdateDayCheckRequest request, CharacterResponse result) {
        if (request.getCategory() == DayTodoCategoryEnum.chaos) {
            saveOrDeleteLog(result, LogContent.CHAOS, result.getChaosCheck() == 2,
                    result.getItemLevel() >= 1640 ? "쿠르잔전선 클리어" : "카오스던전 클리어", result.getChaosGold());
        } else if (request.getCategory() == DayTodoCategoryEnum.guardian) {
            saveOrDeleteLog(result, LogContent.GUARDIAN, result.getGuardianCheck() == 1,
                    "가디언토벌 클리어", result.getGuardianGold());
        }
    }

    private void saveOrDeleteLog(CharacterResponse result, LogContent content, boolean shouldSave, String message, double profit) {
        Logs logs = Logs.builder()
                .localDate(LocalDate.now())
                .memberId(result.getMemberId())
                .characterId(result.getCharacterId())
                .logType(LogType.DAILY)
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



