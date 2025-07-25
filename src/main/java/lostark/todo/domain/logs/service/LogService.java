package lostark.todo.domain.logs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.cube.dto.SpendCubeResponse;
import lostark.todo.domain.logs.dto.*;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.repository.LogsRepository;
import lostark.todo.domain.member.dto.LifeEnergySpendRequest;
import lostark.todo.domain.member.entity.LifeEnergy;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.dto.CursorResponse;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogsRepository repository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CharacterRepository characterRepository;

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

            // 전체 체크 시 일일 숙제가 이미 있는 로그가 있다면 deleted true로 변경
            if (logs.getLogContent().equals(LogContent.DAY_CHECK_ALL_CHARACTERS)) {
                for (Logs existLogs : repository.getAll(logs.getMemberId(), logs.getLogType(), logs.getLocalDate())) {
                    if (!existLogs.getLogContent().equals(LogContent.DAY_CHECK_ALL_CHARACTERS)) {
                        existLogs.setDeleted(true);
                    }
                }
            }

        } catch (DataAccessException e) {
            log.error("로그 처리 실패: characterId={}, logContent={}", logs.getCharacterId(), logs.getLogContent(), e);
            throw e;
        }
    }


    private List<Logs> findExistingLogs(Logs logs) {
        if (logs.getLogType().equals(LogType.DAILY)) {
            return repository.get(logs.getMemberId(), logs.getCharacterId(), logs.getLogContent(), logs.getLocalDate(), null);
        } else if (logs.getLogType().equals(LogType.WEEKLY)) {
            repository.get(logs.getMemberId(), logs.getCharacterId(), logs.getLogContent(), logs.getLocalDate(), logs.getName());
        } else if (logs.getLogType().equals(LogType.ETC)) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
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

    // 큐브 소모 로그 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processCubeLog(SpendCubeResponse response) {
        LocalDate logDate = getLocalDate();

        String message = response.getServerName() + " 서버의 " +
                response.getCharacterName() + "(" + response.getItemLevel() + ")" + " 캐릭터가 " + response.getName() + "큐브를 클리어하여 " +
                response.getProfit() + "골드를 획득했습니다.";

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(response.getMemberId())
                .characterId(response.getCharacterId())
                .logType(LogType.ETC)
                .logContent(LogContent.CUBE)
                .name("큐브")
                .message(message)
                .profit(response.getProfit())
                .build();
        eventPublisher.publishEvent(new LogCreatedEvent(logs));
    }

    @Transactional
    public void delete(String username, Long logId) {
        Member member = memberRepository.get(username);
        Optional<Logs> log = repository.findById(logId);
        if (log.isEmpty()) {
            throw new ConditionNotMetException("없는 로그(타임라인) 입니다.");
        } else {
            if (log.get().getMemberId() == member.getId()) {
                log.get().setDeleted(true);
            } else {
                throw new ConditionNotMetException("권한이 없습니다.");
            }
        }
    }

    @Transactional
    public void saveEtcLog(String username, SaveEtcLogRequest request) {
        Character character = characterRepository.getByIdAndUsername(request.getCharacterId(), username).orElseThrow(
                () -> new ConditionNotMetException("캐릭터가 존재하지 않습니다. ID: " + request.getCharacterId() + ", 사용자 이름: " + username));

        String message = Logs.createEtcMessage(character, request.getMessage(), request.getProfit());

        Logs logs = Logs.builder()
                .localDate(request.getLocalDate())
                .memberId(character.getMember().getId())
                .characterId(character.getId())
                .logType(LogType.ETC)
                .logContent(LogContent.ETC)
                .name("기타")
                .message(message)
                .profit(request.getProfit())
                .build();

        saveLog(logs);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processLifeEnergyLog(LifeEnergy lifeEnergy, LifeEnergySpendRequest request) {
        Character character = findCharacterByName(lifeEnergy, request.getCharacterName());
        String logMessage = createLogMessage(character, request);

        Logs logs = createLifeEnergyLog(character, logMessage, request.getGold());
        eventPublisher.publishEvent(new LogCreatedEvent(logs));
    }

    private Character findCharacterByName(LifeEnergy lifeEnergy, String characterName) {
        return lifeEnergy.getMember().getCharacters()
                .stream()
                .filter(character -> character.getCharacterName().equals(characterName))
                .findFirst()
                .orElseThrow(() -> new ConditionNotMetException(
                        "등록되지 않은 캐릭터 이름입니다. 만약 캐릭터 이름이 변경되었다면 생활의 기운 캐릭터를 다시 등록해주세요."
                ));
    }

    private String createLogMessage(Character character, LifeEnergySpendRequest request) {
        return String.format("%s 서버의 %s(%s) 캐릭터가 생활의 기운 %s를 소모하여 %s골드를 획득했습니다.",
                character.getServerName(),
                character.getCharacterName(),
                character.getItemLevel(),
                request.getEnergy(),
                request.getGold()
        );
    }

    private Logs createLifeEnergyLog(Character character, String message, int profit) {
        return Logs.builder()
                .localDate(getLocalDate())
                .memberId(character.getMember().getId())
                .characterId(character.getId())
                .logType(LogType.ETC)
                .logContent(LogContent.ETC)
                .name("생활의 기운")
                .message(message)
                .profit(profit)
                .build();
    }


    // 보상 정보를 담는 내부 클래스
    private record RewardInfo(String gates, int finalGold) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processDayCheckAllCharactersLog(String username, UpdateDayCheckAllCharactersResponse response) {
        LocalDate logDate = getLocalDate();
        Member member = memberRepository.get(username);

        String message = String.format("%s 서버의 모든 캐릭터 일일 숙제 %s",
                response.getServerName(),
                response.isDone() ? "전체 해제" : "전체 완료");

        Logs logs = Logs.builder()
                .localDate(logDate)
                .memberId(member.getId())
                .logType(LogType.DAILY)
                .logContent(LogContent.DAY_CHECK_ALL_CHARACTERS)
                .name(response.getServerName())
                .message(message)
                .profit(response.getProfit())
                .deleted(response.isDone())
                .build();

        eventPublisher.publishEvent(new LogCreatedEvent(logs));
    }
}