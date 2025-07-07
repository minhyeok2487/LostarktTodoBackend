package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.dto.TodoResponseDto;
import lostark.todo.domain.character.dto.UpdateWeekRaidCheckRequest;
import lostark.todo.domain.character.dto.UpdateWeekRaidMoreRewardCheckRequest;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
import lostark.todo.domain.logs.dto.*;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.repository.LogsRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogsRepository repository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public CursorResponse<LogsSearchResponse> search(String username, LogsSearchParams params) {
        PageRequest pageRequest = PageRequest.of(0, 100);
        Member member = memberRepository.get(username);
        return repository.search(member.getId(), params, pageRequest);
    }

    @Transactional
    public List<LogProfitResponse> getProfit(String username, GetLogsProfitRequest request) {
        Member member = memberRepository.get(username);
        return repository.getProfit(member.getId(), request);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Logs logs) {
        try {
            List<Logs> existingLogs = findExistingLogs(logs);

            if (existingLogs.isEmpty()) {
                repository.save(logs);
            } else {
                // 마지막 로그
                Logs lastLog = existingLogs.get(existingLogs.size() - 1);
                lastLog.updateFrom(logs);

                // 나머지 로그들
                List<Logs> otherLogs = existingLogs.subList(0, existingLogs.size() - 1);
                for (Logs log : otherLogs) {
                    log.setDeleted(true);
                }
            }
        } catch (DataAccessException e) {
            log.error("로그 처리 실패: characterId={}, logContent={}", logs.getCharacterId(), logs.getLogContent(), e);
            throw e;
        }
    }


    private List<Logs> findExistingLogs(Logs logs) {
        return (logs.getLogContent().equals(LogContent.CHAOS) || logs.getLogContent().equals(LogContent.GUARDIAN))
                ? repository.get(logs.getCharacterId(), logs.getLogContent(), logs.getLocalDate(), null)
                : repository.get(logs.getCharacterId(), logs.getLogContent(), logs.getLocalDate(), logs.getName());
    }


    // 주간 레이드 수익 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processWeekLog(UpdateWeekRaidCheckRequest request, CharacterResponse response) {
        response.getTodoList().stream()
                .filter(todo -> todo.getWeekCategory().equals(request.getWeekCategory()))
                .forEach(todo -> {
                    int gold = (response.isGoldCharacter() && todo.isGoldCheck()) ? todo.getRealGold() : 0;
                    String message = formatRaidLogMessage(todo, response, gold);
                    saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID, todo.getWeekCategory(), todo.isCheck(), message, gold);

                    // 취소하면 더보기도 초기화
                    if (!todo.isCheck()) {
                        saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID_MORE_REWARD,
                                request.getWeekCategory(), false, "더보기 취소", 0);
                    }

                });
    }

    // 주간 레이드 더보기 로그 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processWeekMoreRewardLog(UpdateWeekRaidMoreRewardCheckRequest request, CharacterResponse response) {
        String weekCategory = request.getWeekCategory();

        TodoResponseDto todo = findTodoByWeekCategory(response.getTodoList(), weekCategory);
        if (todo == null) {
            return;
        }

        RewardInfo rewardInfo = calculateRewardInfo(todo, response.isGoldCharacter());
        String message = createLogMessage(response, weekCategory, rewardInfo);

        saveCharacterResponseLog(response, LogType.WEEKLY, LogContent.RAID_MORE_REWARD,
                weekCategory, rewardInfo.finalGold() < 0, message, rewardInfo.finalGold());
    }


    // 주어진 TodoList에서 weekCategory에 해당하는 Todo를 찾기
    private TodoResponseDto findTodoByWeekCategory(List<TodoResponseDto> todoList, String weekCategory) {
        return todoList.stream()
                .filter(todo -> weekCategory.equals(todo.getWeekCategory()))
                .findFirst()
                .orElse(null);
    }


    // 더보기 보상 정보를 계산
    private RewardInfo calculateRewardInfo(TodoResponseDto todo, boolean isGoldCharacter) {
        List<Boolean> moreRewardCheckList = todo.getMoreRewardCheckList();
        List<Integer> moreRewardGoldList = todo.getMoreRewardGoldList();

        StringBuilder gates = new StringBuilder();
        int totalGold = 0;

        for (int i = 0; i < moreRewardCheckList.size(); i++) {
            if (moreRewardCheckList.get(i)) {
                if (!gates.isEmpty()) {
                    gates.append(", ");
                }
                gates.append(i + 1);
                totalGold += moreRewardGoldList.get(i);
            }
        }

        // 관문 정보가 있으면 "관문" 텍스트 추가
        String gatesText = !gates.isEmpty() ? (gates + " 관문") : "";

        int finalGold = isGoldCharacter ? -totalGold : 0;
        return new RewardInfo(gatesText, finalGold);
    }


    // 더보기 로그 메시지를 생성
    private String createLogMessage(CharacterResponse response, String weekCategory, RewardInfo rewardInfo) {
        return String.format("%s 서버의 %s(%s) 캐릭터가 %s %s 더보기를 체크하여 %d골드를 소모했습니다.",
                response.getServerName(),
                response.getCharacterName(),
                response.getItemLevel(),
                weekCategory,
                rewardInfo.gates(),
                rewardInfo.finalGold());
    }


    // 보상 정보를 담는 내부 클래스
    private record RewardInfo(String gates, int finalGold) {}


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