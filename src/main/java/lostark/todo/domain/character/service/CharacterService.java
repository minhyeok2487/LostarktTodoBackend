package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dtoV2.character.*;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.util.content.repository.ContentRepository;
import lostark.todo.domain.util.content.service.ContentService;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.character.entity.*;
import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.util.market.service.MarketService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.global.Constant.LEVEL_UP_RESOURCES;
import static lostark.todo.global.exhandler.ErrorMessageConstants.CHARACTER_NOT_FOUND;
import static lostark.todo.global.utils.GlobalMethod.isSameUUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final MemberRepository memberRepository;
    private final MarketRepository marketRepository;
    private final ContentRepository contentRepository;
    private final TodoV2Repository todoV2Repository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final ContentService contentService;
    private final MarketService marketService;

    // 외부 API를 이용하므로 트랜잭션 분리
    public CharacterUpdateContext loadCharacterUpdateResources(String apiKey, String newCharacterName) {
        // 1. 캐릭터 검색
        CharacterJsonDto newCharacter = lostarkCharacterApiClient.getCharacterWithException(
                newCharacterName, apiKey);

        // 2. 필요 데이터 호출(거래소, 통계)
        Map<String, Market> contentResource = marketService.findContentResource();
        Map<Category, List<DayContent>> dayContent = contentService.getDayContent();
        return new CharacterUpdateContext(newCharacter, dayContent, contentResource);
    }

    // ------------------------------------------------------------------------------------------- //

    @Transactional(readOnly = true)
    public Character get(long characterId, String username) {
        return characterRepository.getByIdAndUsername(characterId, username).orElseThrow(
                () -> new ConditionNotMetException("캐릭터가 존재하지 않습니다. ID: " + characterId + ", 사용자 이름: " + username));
    }

    // 캐릭터 일일 컨텐츠 수익 계산(휴식게이지 포함)
    @Transactional
    public void calculateDayTodo(Character character, Map<String, Market> contentResource) {
        character.calculateDayTodo(character, contentResource);
    }

    private void validateGauge(Integer gauge, int max) {
        if (gauge < 0 || gauge > max || gauge % 10 != 0) {
            throw new ConditionNotMetException(String.format("휴식게이지는 0~%d 사이이며, 10단위여야 합니다.", max));
        }
    }

    @Transactional
    public Character updateGoldCharacter(CharacterDefaultDto characterDefaultDto, String username) {
        Character character = get(
                characterDefaultDto.getCharacterId(), username);
        return character.updateGoldCharacter();
    }

    @Transactional
    public void updateGoldCharacter(Character character) {
        // 골드 획득 지정 캐릭터 : 서버별 6캐릭 이상인지 확인
        int goldCharacter = characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(
                character.getMember(), character.getServerName());

        //골드획득 지정 캐릭터가 아닌데 6개가 넘으면
        if (!character.isGoldCharacter() && goldCharacter >= 6) {
            throw new ConditionNotMetException("골드 획득 지정 캐릭터는 서버별로 6캐릭까지 가능합니다.");
        }

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
    public Character updateSetting(String username, CharacterSettingRequest characterSettingRequest) {
        Character character = get(characterSettingRequest.getCharacterId(), username);
        character.getSettings().update(characterSettingRequest.getName(), characterSettingRequest.getValue());

        updateRelatedTodos(characterSettingRequest, character);
        return character;
    }

    private void updateRelatedTodos(CharacterSettingRequest characterSettingRequest, Character character) {
        // 더보기 버튼의 설정 값이 변하면 기존 더보기 해제
        if (characterSettingRequest.getName().equals("showMoreButton")) {
            character.getTodoV2List().forEach(todoV2 -> todoV2.setMoreRewardCheck(false));
        }

        // 캐릭터 출력이 false로 변경되면 레이드 골드 체크 해제, 숙제 제거
        if (characterSettingRequest.getName().equals("showCharacter") && characterSettingRequest.getValue().equals(false)) {
            character.setGoldCharacter(false);
            todoV2Repository.removeCharacter(character);
        }
    }

    @Transactional
    public Character addCharacter(CharacterJsonDto dto, DayTodo dayContent, Member member) {
        Character character = Character.builder()
                .member(member)
                .characterName(dto.getCharacterName())
                .characterLevel(dto.getCharacterLevel())
                .characterClassName(dto.getCharacterClassName())
                .serverName(dto.getServerName())
                .itemLevel(dto.getItemMaxLevel())
                .dayTodo(dayContent)
                .weekTodo(new WeekTodo())
                .build();
        character.setSettings(new Settings());
        character.setTodoV2List(new ArrayList<>());
        character.createImage(dto.getCharacterImage());
        return characterRepository.save(character);
    }

    @Transactional
    public Character updateMemo(String username, UpdateMemoRequest updateMemoRequest) {
        Character character = get(updateMemoRequest.getCharacterId(), username);
        return character.updateMemo(updateMemoRequest.getMemo());
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
    public void updateCharacterList(String username) {
        // 1. 회원 조회
        Member member = memberRepository.get(username);

        // 2. 대표 캐릭터 이름 조회
        String mainCharacter = member.getMainCharacterName();

        // 3. 원정대 검색할 캐릭터 닉네임 찾기
        String searchCharacterName = findSiblingCharacterName(member);

        // 4. 콘텐츠 통계 데이터 조회
        Map<String, Market> contentResource = marketRepository.findByNameIn(LEVEL_UP_RESOURCES)
                .stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
        Map<Category, List<DayContent>> dayContents = contentRepository.getDayContents();
        List<DayContent> chaosDungeons = dayContents.get(Category.카오스던전);
        List<DayContent> guardianRaids = dayContents.get(Category.가디언토벌);

        // 5. 원정대 캐릭터 업데이트 로직
        updateSiblings(searchCharacterName, member, chaosDungeons, guardianRaids, contentResource, mainCharacter);
    }

    // 원정대 검색할 캐릭터 닉네임 찾기
    // 캐릭터 이미지가 있으면서 UUID가 일치하는 첫번째 캐릭터
    private String findSiblingCharacterName(Member member) {
        return member.getCharacters().stream()
                .filter(character -> character.getCharacterImage() != null)
                .filter(character -> {
                    CharacterJsonDto updatedCharacter = lostarkCharacterApiClient.getCharacter(character.getCharacterName(), member.getApiKey());
                    return isMatchingCharacter(character, updatedCharacter);
                })
                .map(Character::getCharacterName)
                .findFirst()
                .orElseThrow(() -> new ConditionNotMetException(CHARACTER_NOT_FOUND));
    }


    private boolean isMatchingCharacter(Character character, CharacterJsonDto updatedCharacter) {
        if (updatedCharacter == null || updatedCharacter.getCharacterImage() == null) {
            return false;
        } else {
            return isSameUUID(updatedCharacter.getCharacterImage(), character.getCharacterImage());
        }
    }

    // 원정대 업데이트
    private void updateSiblings(String searchCharacterName, Member member, List<DayContent> chaos, List<DayContent> guardian,
                                Map<String, Market> contentResource, String mainCharacter) {
        List<CharacterJsonDto> siblings = lostarkCharacterApiClient.getSiblings(searchCharacterName, member.getApiKey());

        siblings.stream()
                .map(dto -> {
                    // 캐릭터 이미지 업데이트
                    CharacterJsonDto updatedCharacter = lostarkCharacterApiClient.getCharacter(dto.getCharacterName(), member.getApiKey());
                    if (updatedCharacter != null && updatedCharacter.getCharacterImage() != null) {
                        dto = updatedCharacter;
                    }
                    return dto;
                })
                .forEach(dto -> updateCharacter(dto, member, chaos, guardian, contentResource, mainCharacter));
    }

    private void updateCharacter(CharacterJsonDto dto, Member member, List<DayContent> chaos, List<DayContent> guardian,
                                 Map<String, Market> contentResource, String mainCharacter) {

        List<Character> findCharacterListUUID = member.getCharacters().stream()
                .filter(character -> character.getCharacterImage() != null)
                .filter(character -> isSameUUID(character.getCharacterImage(), dto.getCharacterImage()))
                .toList();

        DayTodo dayContent = new DayTodo().createDayContent(chaos, guardian, dto.getItemMaxLevel());

        if (!findCharacterListUUID.isEmpty()) {
            for (Character character : findCharacterListUUID) {
                // 캐릭터가 존재할 경우 업데이트
                if (character.getCharacterName().equals(mainCharacter)) {
                    member.setMainCharacter(dto.getCharacterName()); // 메인 캐릭터 이름 변경
                }
                character.updateCharacter(dto, dayContent, contentResource);
            }
        } else {
            // UUID가 일치하지 않으면 이름으로 캐릭터 찾기
            List<Character> findCharacterListName = member.getCharacters().stream()
                    .filter(character -> character.getCharacterName().equals(dto.getCharacterName()))
                    .toList();

            if (!findCharacterListName.isEmpty()) {
                // 이름으로 찾은 캐릭터가 있으면 업데이트
                for (Character character : findCharacterListName) {
                    character.updateCharacter(dto, dayContent, contentResource);
                }
            } else {
                // UUID도, 이름도 일치하는 캐릭터가 없으면 새로 추가
                Character newCharacter = addCharacter(dto, dayContent, member);
                calculateDayTodo(newCharacter, contentResource);
                member.getCharacters().add(newCharacter);
            }
        }
    }

    //    ----------------------------------------------------------------------------------------------------------------
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
    }

    @Transactional
    public void updateDayGauge(Character character, UpdateDayGaugeRequest request) {
        Integer dtoChaosGauge = request.getChaosGauge();
        validateGauge(dtoChaosGauge, 200); //검증

        Integer dtoGuardianGauge = request.getGuardianGauge();
        validateGauge(dtoGuardianGauge, 100); //검증

        Integer dtoEponGauge = request.getEponaGauge();
        validateGauge(dtoEponGauge, 100); //검증

        character.getDayTodo().updateDayContentGauge(request);

        character.calculateDayTodo(marketRepository.findByNameIn(LEVEL_UP_RESOURCES).stream()
                .collect(Collectors.toMap(Market::getName, market -> market)));
    }

    @Transactional(readOnly = true)
    public List<DeletedCharacterResponse> getDeletedCharacter(String username) {
        return characterRepository.getDeletedCharacter(username);
    }

    @Transactional
    public void updateDeletedCharacter(Character character) {
        character.updateCharacterStatus();
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
}
