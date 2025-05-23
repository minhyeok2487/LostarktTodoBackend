package lostark.todo.domain.logs.customAnnotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.service.LogService;
import lostark.todo.domain.cube.dto.SpendCubeResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.task.TaskExecutor;
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
    private final LogService logService;
    private final TaskExecutor taskExecutor;

    @AfterReturning(pointcut = "@annotation(loggable)", returning = "resultObject")
    public void characterResponseLogs(JoinPoint joinPoint, Object resultObject, Loggable loggable) {
        taskExecutor.execute(() -> {
            if (!(resultObject instanceof ResponseEntity<?> responseEntity)) {
                log.warn("Unexpected return type: {}", resultObject.getClass().getName());
                return;
            }
            String category = loggable.category().trim();
            if (category.equalsIgnoreCase("CUBE")) {
                handleCubeLogs(responseEntity);
            } else {
                handleCharacterLogs(joinPoint, responseEntity);
            }
        });
    }

    private void handleCharacterLogs(JoinPoint joinPoint, ResponseEntity<?> responseEntity) {
        if (!(responseEntity.getBody() instanceof CharacterResponse response)) {
            log.warn("Unexpected response body type: {}", Objects.requireNonNull(responseEntity.getBody()).getClass().getName());
            return;
        }

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UpdateDayCheckRequest request) {
                processDayLog(request.getCategory(), response);
            } else if (arg instanceof UpdateWeekRaidCheckRequest request) {
                processWeekLog(request, response);
            } else if (arg instanceof UpdateWeekRaidMoreRewardCheckRequest request) {
                processWeekMoreRewardLog(request, response);
            } else if (arg instanceof UpdateDayCheckAllRequest){
                processDayLog(DayTodoCategoryEnum.chaos, response);
                processDayLog(DayTodoCategoryEnum.guardian, response);
            }
        }
    }

    private void handleCubeLogs(ResponseEntity<?> responseEntity) {
        if (!(responseEntity.getBody() instanceof SpendCubeResponse response)) {
            log.warn("Unexpected response body type: {}", Objects.requireNonNull(responseEntity.getBody()).getClass().getName());
            return;
        }
        processCubeLog(response);
    }

    private void processCubeLog(SpendCubeResponse response) {
        String message = response.getServerName() + " 서버의 " +
                response.getCharacterName() + "(" + response.getItemLevel() + ")" + " 캐릭터가 " + response.getName() + "큐브를 클리어하여 " +
                response.getProfit() + "골드를 획득했습니다.";
        saveCubeLog(response, message, response.getProfit());
    }

    private void processWeekMoreRewardLog(UpdateWeekRaidMoreRewardCheckRequest request, CharacterResponse response) {
        StringBuilder message = new StringBuilder(response.getServerName() + " 서버의 " +
                response.getCharacterName() + "(" + response.getItemLevel() + ")" + " 캐릭터가 ");
        response.getTodoList().stream()
                .filter(todo -> todo.getWeekCategory().equals(request.getWeekCategory()))
                .forEach(todo -> {
                    for (int i = 0; i < todo.getMoreRewardCheckList().size(); i++) {
                        if (i == request.getGate() - 1) {
                            int gold = response.isGoldCharacter() ? -todo.getMoreRewardGoldList().get(i) : 0;
                            message.append(todo.getWeekCategory()).append(" ").append(request.getGate()).append("관문 더보기를 체크하여 ");
                            message.append(gold).append("골드를 소모했습니다.");
                            saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID_MORE_REWARD,
                                    todo.getWeekCategory(), todo.getMoreRewardCheckList().get(i), message.toString(), gold);
                            break;
                        }
                    }
                });
    }

    private void processWeekLog(UpdateWeekRaidCheckRequest request, CharacterResponse response) {
        response.getTodoList().stream()
                .filter(todo -> todo.getWeekCategory().equals(request.getWeekCategory()))
                .forEach(todo -> {
                    int gold = (response.isGoldCharacter() && todo.isGoldCheck()) ? todo.getRealGold() : 0;
                    String message = formatRaidLogMessage(todo, response, gold);
                    saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID, todo.getWeekCategory(), todo.isCheck(), message, gold);

                    // 취소하면 더보기도 초기화
                    if (!todo.isCheck()) {
                        logService.deleteMoreRewardLogs(response.getMemberId(), response.getCharacterId(), request.getWeekCategory());
                    }
                });
    }

    private void processDayLog(DayTodoCategoryEnum category, CharacterResponse response) {
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

    private void saveCharacterResponseLog(CharacterResponse result, LogType logType, LogContent content, String name,
                                          boolean shouldSave, String message, double profit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetTime = now.toLocalDate().atStartOfDay().plusHours(6);
        LocalDate logDate = now.isBefore(resetTime) ? now.toLocalDate().minusDays(1) : now.toLocalDate();

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(result.getMemberId())
                .characterId(result.getCharacterId())
                .logType(logType)
                .logContent(content)
                .name(name)
                .message(message)
                .profit(profit)
                .build();

        if (shouldSave) {
            service.saveLog(logs);
        } else {
            service.deleteLog(logs);
        }
    }

    private void saveCubeLog(SpendCubeResponse result, String message, double profit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetTime = now.toLocalDate().atStartOfDay().plusHours(6);
        LocalDate logDate = now.isBefore(resetTime) ? now.toLocalDate().minusDays(1) : now.toLocalDate();

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(result.getMemberId())
                .characterId(result.getCharacterId())
                .logType(LogType.ETC)
                .logContent(LogContent.CUBE)
                .name("큐브")
                .message(message)
                .profit(profit)
                .build();
        service.saveLog(logs);

    }
}
