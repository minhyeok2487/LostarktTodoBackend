package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.domain.admin.dto.DashboardResponse;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.content.service.ContentService;
import lostark.todo.domain.character.entity.*;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.logs.service.LogService;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final TodoV2Repository todoV2Repository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final ContentService contentService;
    private final MarketService marketService;
    private final LogService logService;

    // 외부 API를 이용하므로 트랜잭션 분리
    public CharacterUpdateContext loadCharacterUpdateResources(String apiKey, String newCharacterName) {
        // 1. 캐릭터 검색
        CharacterJsonDto newCharacter = lostarkCharacterApiClient.getCharacter(
                newCharacterName, apiKey);

        // 2. 필요 데이터 호출(거래소, 통계)
        Map<String, Market> contentResource = marketService.findLevelUpResource();
        Map<Category, List<DayContent>> dayContent = contentService.getDayContent();
        return new CharacterUpdateContext(newCharacter, dayContent, contentResource);
    }

    @Transactional(readOnly = true)
    public Character get(long characterId, String username) {
        return characterRepository.getByIdAndUsername(characterId, username).orElseThrow(
                () -> new ConditionNotMetException("캐릭터가 존재하지 않습니다. ID: " + characterId + ", 사용자 이름: " + username));
    }

    private void validateGauge(Integer gauge, int max) {
        if (gauge < 0 || gauge > max || gauge % 10 != 0) {
            throw new ConditionNotMetException(String.format("휴식게이지는 0~%d 사이이며, 10단위여야 합니다.", max));
        }
    }

    // 골드 획득 캐릭터 지정/해제
    @Transactional
    public void updateGoldCharacter(Character character) {
        character.updateGoldCharacter();
    }

    /**
     * 실마엘 교환 업데이트
     */
    @Transactional
    public void updateWeekSilmael(Character character) {
        character.getWeekTodo().updateSilmael();
    }

    /**
     * 큐브 티켓 업데이트
     */
    @Transactional
    public void updateCubeTicket(Character character, int num) {
        character.getWeekTodo().updateCubeTicket(num);
    }

    @Transactional
    public boolean deleteByMember(Member member) {
        long result = characterRepository.deleteByMember(member);
        return result != 0;
    }

    // 캐릭터 주간 레이드 골드 체크 업데이트
    // 주간 레이드 골드 체크 업데이트 전 체크사항
    private void raidGoldCheckCount(Character character, boolean updateValue) {
        List<String> weekCategoryList = character.getTodoV2List().stream()
                .filter(TodoV2::isGoldCheck)
                .map(todoV2 -> todoV2.getWeekContent().getWeekCategory())
                .distinct()
                .toList();


        // 등록된 골드획득 컨텐츠가 3개 이상 && 또다른 레이드를 골드획득 체크를 한다면(true)
        if (weekCategoryList.size() >= 3 && updateValue) {
            throw new ConditionNotMetException("골드 획득은 3개까지 가능합니다.");
        }
    }

    @Transactional
    public void updateRaidGoldCheck(Character character, String weekCategory, boolean updateValue) {
        raidGoldCheckCount(character, updateValue);

        List<TodoV2> todoV2List = character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getWeekContent().getWeekCategory().equals(weekCategory))
                .toList();

        // 레이드를 등록한 후 -> 골드 획득을 지정
        if (todoV2List.isEmpty()) {
            throw new ConditionNotMetException("레이드를 먼저 등록해주십시오.");
        }

        todoV2List.forEach(TodoV2::updateGoldCheck);
    }

    @Transactional
    public void updateGoldCheckVersion(Character character) {
        character.getSettings().updateGoldCheckVersion();
    }

    @Transactional
    public List<DashboardResponse> searchCharactersDashBoard(int limit) {
        return characterRepository.searchCharactersDashBoard(limit);
    }

    @Transactional
    public void updateSetting(Character character, UpdateCharacterSettingRequest updateCharacterSettingRequest) {
        character.getSettings().update(updateCharacterSettingRequest.getName(), updateCharacterSettingRequest.getValue());

        updateRelatedTodos(updateCharacterSettingRequest, character);
    }

    private void updateRelatedTodos(UpdateCharacterSettingRequest updateCharacterSettingRequest, Character character) {
        // 더보기 버튼의 설정 값이 변하면 기존 더보기 해제
        if (updateCharacterSettingRequest.getName().equals("showMoreButton")) {
            character.getTodoV2List().forEach(todoV2 -> todoV2.setMoreRewardCheck(false));
        }

        // 캐릭터 출력이 false로 변경되면 레이드 골드 체크 해제, 숙제 제거
        if (updateCharacterSettingRequest.getName().equals("showCharacter") && updateCharacterSettingRequest.getValue().equals(false)) {
            character.setGoldCharacter(false);
            todoV2Repository.removeCharacter(character);
        }
    }

    @Transactional
    public void updateMemo(Character character, String memo) {
        character.updateMemo(memo);
    }

    @Transactional
    public void delete(Long characterId, String username) {
        Character character = get(characterId, username);
        if (character.isGoldCharacter()) {
            throw new ConditionNotMetException("골드 획득 캐릭터는 삭제 할 수 없습니다.");
        }
        character.updateCharacterStatus();
    }

    @Transactional
    public void delete(Character character) {
        if (character.isGoldCharacter()) {
            throw new ConditionNotMetException("골드 획득 캐릭터는 삭제 할 수 없습니다.");
        }
        character.updateCharacterStatus();
    }

    @Transactional
    public List<CharacterResponse> convertAndSortCharacterList(List<Character> characterList) {
        return characterList.stream()
                .map(new CharacterResponse()::toDto) // DTO로 변환
                .sorted(
                        Comparator.comparingInt(CharacterResponse::getSortNumber)
                                .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed())
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharacterList(String username) {
        List<Character> characterList = characterRepository.getCharacterList(username);
        return convertAndSortCharacterList(characterList);
    }

    @Transactional
    public List<CharacterResponse> editSort(String username, List<CharacterSortRequest> characterSortRequestList) {
        List<Character> characterList = characterRepository.getCharacterList(username).stream().peek(
                        character -> characterSortRequestList.stream()
                                .filter(characterSortRequest -> character.getCharacterName().equals(characterSortRequest.getCharacterName()))
                                .findFirst()
                                .ifPresent(characterSortRequest -> character.setSortNumber(characterSortRequest.getSortNumber())))
                .toList();
        return convertAndSortCharacterList(characterList);

    }

    @Transactional
    public void updateWeekEpona(Character character, UpdateWeekEponaRequest request) {
        Optional.of(character.getWeekTodo())
                .filter(weekTodo -> request.isAllCheck() && weekTodo.getWeekEpona() < 3)
                .ifPresent(weekTodo -> weekTodo.setWeekEpona(2));

        character.getWeekTodo().updateWeekEpona();
    }

    @Transactional
    public void updateDayCheck(Character character, UpdateDayCheckRequest request) {
        DayTodo dayTodo = character.getDayTodo();

        switch (request.getCategory()) {
            case epona -> {
                if (request.isAllCheck()) {
                    dayTodo.updateCheckEponaAll();
                } else {
                    dayTodo.updateCheckEpona();
                }
            }
            case chaos -> dayTodo.updateCheckChaos();
            case guardian -> dayTodo.updateCheckGuardian();
            default -> throw new IllegalArgumentException("Invalid day todo category: " + request.getCategory());
        }

        logService.processDayLog(request.getCategory(), character);
    }

    @Transactional
    public void updateDayGauge(Character character, UpdateDayGaugeRequest request) {
        character.getDayTodo().updateDayContentGauge(request);
        character.getDayTodo().calculateDayTodo(character, marketService.findLevelUpResource());
    }

    public void validateUpdateDayGauge(UpdateDayGaugeRequest request) {
        validateGauge(request.getChaosGauge(), 200); //카오스 게이지 검증
        validateGauge(request.getGuardianGauge(), 100); //가디언 게이지 검증
        validateGauge(request.getEponaGauge(), 100); //에포나 게이지 검증
    }

    @Transactional(readOnly = true)
    public List<DeletedCharacterResponse> getDeletedCharacter(String username) {
        return characterRepository.getDeletedCharacter(username);
    }

    /**
     * 일일 컨텐츠 전체 체크 업데이트
     * 출력된 상태인 컨텐츠가 하나라도 체크된 상태면 전체 false
     * 출력된 상태인 컨텐츠 중 전체 체크가 아닌 상태면 전체 true
     */
    @Transactional
    public void updateDayCheckAll(Character updateCharacter) {
        DayTodo dayTodo = updateCharacter.getDayTodo();
        Settings settings = updateCharacter.getSettings();

        // 현재 캐릭터의 일일 컨텐츠 상태를 기반으로 ContentUpdater 리스트 생성
        List<ContentUpdater> updaters = ContentUpdater.toDto(dayTodo, settings);

        // 모든 컨텐츠가 완료 상태인지 확인
        boolean checkAllCompleted = isCheckAllCompleted(updaters);

        // 전체 체크 상태를 반영
        calculateUpdateDayCheckAll(updaters, checkAllCompleted);
    }

    /**
     * 출력된 컨텐츠(활성화된 컨텐츠) 중 모든 항목이 체크 완료 상태인지 확인
     * - 하나라도 체크 해제된 항목이 있으면 false 반환
     *
     * @param updaters 컨텐츠 리스트
     * @return 모든 컨텐츠가 완료 상태이면 true, 하나라도 미완료면 false
     */
    private boolean isCheckAllCompleted(List<ContentUpdater> updaters) {
        return updaters.stream()
                .filter(ContentUpdater::isDisplayed)
                .allMatch(ContentUpdater::isChecked);
    }

    /**
     * 전체 체크 상태를 업데이트
     * - 모든 컨텐츠가 체크 완료 상태이면 -> 활성화 된 전체 체크 해제 (0)
     * - 하나라도 체크 해제된 상태이면 -> 전체 체크 (각 컨텐츠의 완료 값으로 설정)
     *
     * @param updaters          컨텐츠 리스트
     * @param checkAllCompleted 전체 체크 여부
     */
    private void calculateUpdateDayCheckAll(List<ContentUpdater> updaters, boolean checkAllCompleted) {
        updaters.stream()
                .filter(ContentUpdater::isDisplayed) // 표시되는 컨텐츠만 처리
                .forEach(updater -> {
                    // 전체 체크면 완료 값(1, 2, 3), 아니면 0
                    updater.updateCheck(checkAllCompleted ? updater.getCompletedValue() : 0);
                    updater.runUpdateMethod(); // 변경 사항을 업데이트
                });
    }

    // 캐릭터 업데이트
    @Transactional
    public void updateCharacter(Character character, CharacterUpdateContext updateContext) {
        character.updateCharacter(updateContext);
    }

    // 캐릭터 이름 업데이트
    @Transactional
    public void updateCharacterName(Character character, CharacterUpdateContext updateContext) {
        character.updateCharacterName(updateContext.getNewCharacter().getCharacterName());
        updateCharacter(character, updateContext);
    }

    // 캐릭터 추가
    @Transactional
    public void addCharacter(Member member, CharacterUpdateContext characterUpdateContext) {
        Character character = new Character().toEntity(member, characterUpdateContext);
        characterRepository.save(character);
    }

    // 캐릭터 상태 변경(삭제, 복구)
    @Transactional
    public void updateCharacterStatus(Character character) {
        character.updateCharacterStatus();
    }

    // 전체 캐릭터 일일컨텐츠 전체 체크(출력된 것만)
    @Transactional
    public UpdateDayCheckAllCharactersResponse updateDayCheckAllCharacters(String username, String serverName) {
        List<Character> characterList = characterRepository.getCharacterList(username);

        // 출력 캐릭터 필터링 (isShowCharacter && 서버 필터)
        List<Character> displayedCharacters = characterList.stream()
                .filter(c -> c.getSettings().isShowCharacter())
                .filter(c -> serverName.equals("전체") || c.getServerName().equals(serverName))
                .toList();

        // 캐릭터별 표시되는 컨텐츠 리스트를 미리 계산 (중복 방지)
        Map<Character, List<ContentUpdater>> updaterMap = displayedCharacters.stream()
                .collect(Collectors.toMap(
                        c -> c,
                        c -> ContentUpdater.toDto(c.getDayTodo(), c.getSettings()).stream()
                                .filter(ContentUpdater::isDisplayed)
                                .toList()
                ));

        // 전체 체크 상태인지 판별 (모든 컨텐츠가 체크 완료)
        boolean allCompleted = updaterMap.values().stream()
                .flatMap(List::stream)
                .allMatch(ContentUpdater::isChecked);

        // 체크 상태를 업데이트
        for (Map.Entry<Character, List<ContentUpdater>> entry : updaterMap.entrySet()) {
            for (ContentUpdater updater : entry.getValue()) {
                updater.updateCheck(allCompleted ? updater.getCompletedValue() : 0); // 전체 체크면 해제, 아니면 완료
                updater.runUpdateMethod(); // 변경 반영
            }
        }

        return new UpdateDayCheckAllCharactersResponse(serverName, allCompleted);
    }


}
