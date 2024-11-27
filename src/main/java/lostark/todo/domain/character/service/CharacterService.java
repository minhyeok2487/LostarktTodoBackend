package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domain.util.content.repository.ContentRepository;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.character.dto.UpdateDayCheckRequest;
import lostark.todo.domain.character.dto.UpdateDayGaugeRequest;
import lostark.todo.domain.character.dto.UpdateWeekEponaRequest;
import lostark.todo.domain.character.entity.*;
import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.enums.ChallengeContentEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
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
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final MemberRepository memberRepository;
    private final MarketRepository marketRepository;
    private final ContentRepository contentRepository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;


    // 캐릭터 조회(member에 포함된 캐릭터인지 검증, id 형식)
    // 로그인한 아이디에 등록된 캐릭터인지 검증
    @Transactional(readOnly = true)
    public Character get(long characterId, String characterName, String username) {
        return characterRepository.getByIdAndUsername(characterId, username).orElseThrow(
                () -> new IllegalArgumentException("characterName = " + characterName + " / username = " + username + " : 존재하지 않는 캐릭터"));
    }

    @Transactional(readOnly = true)
    public Character get(long characterId, String username) {
        return characterRepository.getByIdAndUsername(characterId, username).orElseThrow(
                () -> new IllegalArgumentException("캐릭터가 존재하지 않습니다. ID: " + characterId + ", 사용자 이름: " + username));
    }

    // 캐릭터 일일 컨텐츠 수익 계산(휴식게이지 포함)
    @Transactional
    public Character calculateDayTodo(Character character, Map<String, Market> contentResource) {
        return character.calculateDayTodo(character, contentResource);
    }

    private void validateGauge(Integer gauge, int max) {
        if (gauge < 0 || gauge > max || gauge % 10 != 0) {
            throw new IllegalArgumentException(String.format("휴식게이지는 0~%d 사이이며, 10단위여야 합니다.", max));
        }
    }

    @Transactional
    public Character updateGoldCharacter(CharacterDefaultDto characterDefaultDto, String username) {
        Character character = get(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        // 골드 획득 지정 캐릭터 : 서버별 6캐릭 이상인지 확인
        int goldCharacter = characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(
                character.getMember(), character.getServerName());

        //골드획득 지정 캐릭터가 아닌데 6개가 넘으면
        if (!character.isGoldCharacter() && goldCharacter >= 6) {
            throw new IllegalArgumentException("골드 획득 지정 캐릭터는 6캐릭까지 가능합니다.");
        }
        return character.updateGoldCharacter();
    }

    public List<Character> findCharacterListServerName(Member member, String serverName) {
        return characterRepository.findCharacterListServerName(member, serverName);
    }

    public List<Character> updateChallenge(Member member, String serverName, ChallengeContentEnum content) {
        List<Character> characterList = findCharacterListServerName(member, serverName);
        for (Character character : characterList) {
            character.updateChallenge(content);
        }
        return characterList;
    }

    /**
     * 실마엘 교환 업데이트
     */
    public void updateWeekSilmael(Character character) {
        character.getWeekTodo().updateSilmael();
    }

    /**
     * 큐브 티켓 업데이트
     */
    public void updateCubeTicket(Character character, int num) {
        character.getWeekTodo().updateCubeTicket(num);
    }


    public boolean deleteByMember(Member member) {
        long result = characterRepository.deleteByMember(member);
        if (result != 0) {
            return true;
        } else {
            return false;
        }
    }

    // 캐릭터 주간 레이드 골드 체크 업데이트
    // 주간 레이드 골드 체크 업데이트 전 체크사항
    private void raidGoldCheckCount(Character character, boolean updateValue) {
        List<String> weekCategoryList = character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.isGoldCheck())
                .map(todoV2 -> todoV2.getWeekContent().getWeekCategory())
                .distinct()
                .collect(Collectors.toList());

        // 등록된 골드획득 컨텐츠가 3개 이상 && 또다른 레이드를 골드획득 체크를 한다면(true)
        if (weekCategoryList.size() >= 3 && updateValue) {
            throw new IllegalArgumentException("골드 획득은 3개까지 가능합니다.");
        }
    }

    @Transactional
    public void updateRaidGoldCheck(Character character, String weekCategory, boolean updateValue) {
        raidGoldCheckCount(character, updateValue);
        long count = character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getWeekContent().getWeekCategory().equals(weekCategory))
                .peek(TodoV2::updateGoldCheck)
                .count();

        // 레이드를 등록한 다음 -> 골드 획득 지정
        if (count == 0) {
            throw new IllegalArgumentException("레이드를 먼저 등록해주십시오.");
        }
    }


    public Character updateGoldCheckVersion(Character character) {
        character.getSettings().updateGoldCheckVersion();
        return character;
    }

    public List<CharacterDto> updateDtoSortedList(List<Character> characterList) {
        return characterList.stream()
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.toList());
    }

    // 주간 레이드 수익을 계산하는 메서드
    public double calculateWeekTotalGold(List<Character> characterList) {
        double weekTotalGold = 0;
        for (Character character : characterList) {
            if (!character.getTodoV2List().isEmpty()) {
                for (TodoV2 todoV2 : character.getTodoV2List()) {
                    if (todoV2.isChecked()) {
                        weekTotalGold += todoV2.getGold();
                    }
                }
            }
        }
        return weekTotalGold;
    }

    // 일간 총 수익을 계산하는 메서드
    public double calculateDayTotalGold(List<Character> characterList) {
        return characterList.stream()
                .mapToDouble(character -> character.getDayTodo().getWeekTotalGold())
                .sum();
    }

    public List<DashboardResponse> searchCharactersDashBoard(int limit) {
        return characterRepository.searchCharactersDashBoard(limit);
    }

    @Transactional
    public Character updateSetting(String username, SettingRequestDto settingRequestDto) {
        Character character = get(
                settingRequestDto.getCharacterId(), settingRequestDto.getCharacterName(), username);
        character.getSettings().update(settingRequestDto.getName(), settingRequestDto.isValue());
        return character;
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
    public void delete(Long characterId, String username) {
        Character character = get(characterId, username);
        character.delete();
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
                .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND));
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
    public List<CharacterResponse> convertAndSortCharacterList(List<Character> characterList) {
        return characterList.stream()
                .map(CharacterResponse::toDto) // DTO로 변환
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
    public List<CharacterResponse> editSort(String username, List<CharacterSortDto> characterSortDtoList) {
        List<Character> characterList = characterRepository.getCharacterList(username).stream().peek(
                        character -> characterSortDtoList.stream()
                                .filter(characterSortDto -> character.getCharacterName().equals(characterSortDto.getCharacterName()))
                                .findFirst()
                                .ifPresent(characterSortDto -> character.setSortNumber(characterSortDto.getSortNumber())))
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
            case chaos -> {
                if (request.isAllCheck()) {
                    dayTodo.updateCheckChaosAll();
                } else {
                    dayTodo.updateCheckChaos();
                }
            }
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
}
