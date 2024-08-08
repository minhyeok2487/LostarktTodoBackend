package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.SettingRequestDto;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domain.character.*;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todoV2.TodoV2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    public List<Character> findCharacterListUsername(String username) {
        return characterRepository.findAllByUsername(username);
    }

    public List<Character> findAll() {
        return characterRepository.findAll();
    }


    // 캐릭터 조회(member에 포함된 캐릭터인지 검증, id 형식)
    // 로그인한 아이디에 등록된 캐릭터인지 검증
    @Transactional(readOnly = true)
    public Character findCharacter(long characterId, String characterName, String username) {
        return characterRepository.getByIdAndUsername(characterId, username).orElseThrow(
                () -> new IllegalArgumentException("characterName = "+characterName+" / username = " + username + " : 존재하지 않는 캐릭터"));
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

    // 일일컨텐츠 휴식게이지 업데이트
    @Transactional
    public Character updateGauge(Character character, CharacterDayTodoDto characterDayTodoDto, Map<String, Market> contentResource) {
        Integer dtoChaosGauge = characterDayTodoDto.getChaosGauge();
        validateGauge(dtoChaosGauge, 200); //검증

        Integer dtoGuardianGauge = characterDayTodoDto.getGuardianGauge();
        validateGauge(dtoGuardianGauge, 100); //검증

        Integer dtoEponGauge = characterDayTodoDto.getEponaGauge();
        validateGauge(dtoEponGauge, 100); //검증

        character.getDayTodo().updateDayContentGauge(characterDayTodoDto);

        return character.calculateDayTodo(character, contentResource);
    }

    private void validateGauge(Integer gauge, int max) {
        if (gauge < 0 || gauge > max || gauge % 10 != 0) {
            throw new IllegalArgumentException(String.format("휴식게이지는 0~%d 사이이며, 10단위여야 합니다.", max));
        }
    }



    // 일일 컨텐츠 체크 업데이트
    public Character updateCheck(Character character, String category) {
        if(category.equals("epona")) {
            character.getDayTodo().updateCheckEpona();
        }
        if(category.equals("chaos")) {
            character.getDayTodo().updateCheckChaos();
        }
        if(category.equals("guardian")) {
            character.getDayTodo().updateCheckGuardian();
        }
        return character;
    }

    public Character updateCheckAll(Character character, String category) {
        if(category.equals("epona")) {
            character.getDayTodo().updateCheckEponaAll();
        }
        if(category.equals("chaos")) {
            character.getDayTodo().updateCheckChaosAll();
        }
        if(category.equals("guardian")) {
            character.getDayTodo().updateCheckGuardian();
        }
        return character;
    }

    @Transactional
    public Character updateGoldCharacter(CharacterDefaultDto characterDefaultDto, String username) {
        Character character = findCharacter(
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
     * 주간 에포나 체크 업데이트
     */
    public void updateWeekEpona(Character character) {
        character.getWeekTodo().updateWeekEpona();
    }

    /**
     * 실마엘 교환 업데이트
     */
    public void updateSilmael(Character character) {
        character.getWeekTodo().updateSilmael();
    }

    /**
     * 큐브 티켓 업데이트
     */
    public void updateCubeTicket(Character character, int num) {
        character.getWeekTodo().updateCubeTicket(num);
    }


    /**
     * 캐릭터 검색(리스트)
     */
    public List<Character> findCharacter(String characterName) {
        return characterRepository.findAllByCharacterName(characterName);
    }

    public Character findCharacterById(long characterId) {
        return characterRepository.findById(characterId).orElseThrow(() -> new IllegalArgumentException("캐릭터 id 에러"));
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
        Character character = findCharacter(
                settingRequestDto.getCharacterId(), settingRequestDto.getCharacterName(), username);
        character.getSettings().update(settingRequestDto.getName(), settingRequestDto.isValue());
        return character;
    }

    // 일일 컨텐츠 체크 업데이트
    @Transactional
    public Character updateDayTodoCheck(String username, CharacterDefaultDto characterDefaultDto,
                                        DayTodoCategoryEnum category, boolean updateAll) {

        Character character = findCharacter(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        DayTodo dayTodo = character.getDayTodo();

        switch (category) {
            case epona -> {
                if (updateAll) {
                    dayTodo.updateCheckEponaAll();
                } else {
                    dayTodo.updateCheckEpona();
                }
            }
            case chaos -> {
                if (updateAll) {
                    dayTodo.updateCheckChaosAll();
                } else {
                    dayTodo.updateCheckChaos();
                }
            }
            case guardian -> dayTodo.updateCheckGuardian();
            default -> throw new IllegalArgumentException("Invalid day todo category: " + category);
        }

        return character;
    }

    public Character updateGauge(String username, CharacterDayTodoDto characterDayTodoDto, Map<String, Market> contentResource) {
        Character character = findCharacter(
                characterDayTodoDto.getCharacterId(), characterDayTodoDto.getCharacterName(), username);
        return updateGauge(character, characterDayTodoDto, contentResource);
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
        character.setTodoList(new ArrayList<>());
        character.setTodoV2List(new ArrayList<>());
        character.createImage(dto.getCharacterImage());
        return characterRepository.save(character);
    }

    @Transactional
    public void updateCharacter(Character character, CharacterJsonDto dto, DayTodo dayContent, Map<String, Market> contentResource) {
        character.updateCharacter(dto, dayContent, contentResource);
    }

    @Transactional
    public Character updateMemo(String username, UpdateMemoRequest updateMemoRequest) {
        Character character = get(updateMemoRequest.getCharacterId(), username);
        return character.updateMemo(updateMemoRequest.getMemo());
    }

    @Transactional
    public void delete(Long characterId, String username) {
        Character character = get(characterId, username);
        characterRepository.delete(character);
    }
}
