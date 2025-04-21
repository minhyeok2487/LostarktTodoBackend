package lostark.todo.controller.dtoV2.character;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.dto.TodoResponseDto;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.content.enums.WeekContentCategory;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponse {

    private long characterId;

    private long memberId;

    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(notes = "서버 이름")
    private String serverName;

    @ApiModelProperty(notes = "정렬 번호")
    private int sortNumber;

    @ApiModelProperty(notes = "카오스 컨텐츠")
    private DayContent chaos;

    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private int chaosCheck;

    @ApiModelProperty(notes = "카오스던전 휴식게이지, 최소 0, 최대 100, 10단위 증가")
    private int chaosGauge;

    @ApiModelProperty(notes = "카오스던전 숙제 완료 시 예상 수익")
    private double chaosGold;

    @ApiModelProperty(notes = "가디언토벌 컨텐츠")
    private DayContent guardian;

    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 1, 1씩 증감")
    private int guardianCheck;

    @ApiModelProperty(notes = "가디언토벌 휴식게이지, 최소 0, 최대 100, 10씩 증감")
    private int guardianGauge;

    @ApiModelProperty(notes = "가디언토벌 숙제 완료 시 예상 수익")
    private double guardianGold;

    @ApiModelProperty(notes = "에포나 의뢰 체크, 최소 0, 최대 3")
    private int eponaCheck;

    @ApiModelProperty(notes = "에포나 의뢰 휴식 게이지")
    private int eponaGauge;

    @ApiModelProperty(notes = "골드획득 지정")
    private boolean goldCharacter;

    @ApiModelProperty(notes = "도전 가디언 토벌")
    private boolean challengeGuardian;

    @ApiModelProperty(notes = "도전 어비스 던전")
    private boolean challengeAbyss;

    @ApiModelProperty(notes = "캐릭터 설정")
    private Settings settings;

    @ApiModelProperty(notes = "주간 에포나 체크, 최소 0, 최대 3")
    private int weekEpona;

    @ApiModelProperty(notes = "실마엘 교환 체크")
    private boolean silmaelChange;

    @ApiModelProperty(notes = "큐브 티켓 갯수")
    private int cubeTicket;

    @ApiModelProperty(notes = "일일 숙제 주간 수익")
    private double weekDayTodoGold;

    @ApiModelProperty(notes = "주간 레이드 골드")
    private int weekRaidGold;

    @ApiModelProperty(notes = "주간 숙제 리스트")
    private List<TodoResponseDto> todoList;

    @Size(max = 100)
    private int beforeEponaGauge; //이전 에포나 휴식게이지(0~100)

    @Size(max = 200)
    private int beforeChaosGauge; //이전 카오스던전 휴식게이지(0~200)

    @Size(max = 100)
    private int beforeGuardianGauge; //이전 가디언토벌 휴식게이지(0~100)

    @ApiModelProperty(notes = "캐릭터 메모")
    private String memo;

    public CharacterResponse toDto(Character character) {
        CharacterResponse characterResponse = buildCharacterResponse(character);
        List<TodoResponseDto> todoResponseDtoList = buildTodoResponseDtoList(character);

        calculateWeekRaidGold(character, characterResponse, todoResponseDtoList);
        markCompletedTodos(todoResponseDtoList);
        calculateBusGold(character, todoResponseDtoList, characterResponse);

        characterResponse.setTodoList(todoResponseDtoList);
        return characterResponse;
    }

    private CharacterResponse buildCharacterResponse(Character character) {
        return CharacterResponse.builder()
                .memberId(character.getMember().getId())
                .characterId(character.getId())
                .characterName(character.getCharacterName())
                .characterImage(character.getCharacterImage())
                .characterClassName(character.getCharacterClassName())
                .serverName(character.getServerName())
                .itemLevel(character.getItemLevel())
                .sortNumber(character.getSortNumber())
                .chaosCheck(character.getDayTodo().getChaosCheck())
                .chaosGauge(character.getDayTodo().getChaosGauge())
                .chaos(character.getDayTodo().getChaos())
                .chaosGold(character.getDayTodo().getChaosGold())
                .guardianCheck(character.getDayTodo().getGuardianCheck())
                .guardianGauge(character.getDayTodo().getGuardianGauge())
                .guardian(character.getDayTodo().getGuardian())
                .guardianGold(character.getDayTodo().getGuardianGold())
                .eponaCheck(character.getDayTodo().getEponaCheck2())
                .eponaGauge(character.getDayTodo().getEponaGauge())
                .goldCharacter(character.isGoldCharacter())
                .challengeAbyss(character.isChallengeAbyss())
                .challengeGuardian(character.isChallengeGuardian())
                .weekEpona(character.getWeekTodo().getWeekEpona())
                .silmaelChange(character.getWeekTodo().isSilmaelChange())
                .cubeTicket(character.getWeekTodo().getCubeTicket())
                .settings(character.getSettings())
                .weekDayTodoGold(character.getDayTodo().getWeekTotalGold())
                .memo(character.getMemo())
                .beforeEponaGauge(character.getDayTodo().getBeforeEponaGauge())
                .beforeChaosGauge(character.getDayTodo().getBeforeChaosGauge())
                .beforeGuardianGauge(character.getDayTodo().getBeforeGuardianGauge())
                .build();
    }

    //TodoResponseDto List 생성
    private List<TodoResponseDto> buildTodoResponseDtoList(Character character) {
        //기본 TodoResponseDto List 생성
        List<TodoResponseDto> todoResponseDtoList = createTodoResponseDtos(character);

        Map<String, Map<WeekContentCategory, List<Integer>>> categorizedMap = categorizeTodos(character);
        categorizedMap.forEach((key, value) -> buildResultString(key, value, todoResponseDtoList));
        return todoResponseDtoList;
    }

    //기본 TodoResponseDto List 생성
    private List<TodoResponseDto> createTodoResponseDtos(Character character) {
        List<TodoResponseDto> todoResponseDtos = new ArrayList<>();
        getSortedTodos(character).forEach(todo -> addOrUpdateTodoDto(todo, todoResponseDtos, character.isGoldCharacter()));
        return todoResponseDtos;
    }

    //TodoV2List 정렬(관문 기준 오름차순)
    //coolTime이 1이상인 TodoV2만 정렬(전주 2주기 레이드 체크)
    private List<TodoV2> getSortedTodos(Character character) {
        return character.getTodoV2List().stream()
                .filter(todo -> todo.getCoolTime() >= 1)  //2주기 레이드 확인용
                .sorted(Comparator.comparingLong(todo -> todo.getWeekContent().getGate()))
                .collect(Collectors.toList());
    }

    //WeekCategory가 동일한 TodoResponseDto가 존재하면 update, 없으면 add
    private void addOrUpdateTodoDto(TodoV2 todo, List<TodoResponseDto> dtos, boolean goldCharacter) {
        TodoResponseDto existingDto = findExistingDto(todo, dtos);
        if (existingDto != null) {
            existingDto.updateExistingTodo(todo, goldCharacter);
        } else {
            dtos.add(new TodoResponseDto().toDto(todo, goldCharacter));
        }
    }

    //WeekCategory가 동일한 TodoResponseDto 찾는 Method
    private TodoResponseDto findExistingDto(TodoV2 todo, List<TodoResponseDto> dtos) {
        return dtos.stream()
                .filter(dto -> dto.getWeekCategory().equals(todo.getWeekContent().getWeekCategory()))
                .findFirst()
                .orElse(null);
    }

    //TodoV2List를 WeekCategory(레이드 이름), WeekContentCategory(노말, 하드)로 분류
    private Map<String, Map<WeekContentCategory, List<Integer>>> categorizeTodos(Character character) {
        return character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getCoolTime() >= 1)
                .collect(Collectors.groupingBy(
                        todo -> todo.getWeekContent().getWeekCategory(),
                        Collectors.groupingBy(
                                todo -> todo.getWeekContent().getWeekContentCategory(),
                                Collectors.mapping(
                                        todo -> todo.getWeekContent().getGate(),
                                        Collectors.toList()
                                )
                        )
                ));
    }

    //WeekCategory, WeekContentCategory, Gate를 문자열로 변환하여 TodoResponseDto에 저장
    private void buildResultString(String weekCategory, Map<WeekContentCategory, List<Integer>> weekContentCategoryMap,
                                   List<TodoResponseDto> todoResponseDtos) {
        StringBuilder result = new StringBuilder(weekCategory).append(" <br />");

        weekContentCategoryMap.forEach((weekContentCategory, gates) -> {
            result.append(weekContentCategory).append(" ");
            gates.forEach(gate -> result.append(gate).append(" "));
        });

        todoResponseDtos.stream()
                .filter(dto -> dto.getWeekCategory().equals(weekCategory))
                .findFirst()
                .ifPresent(dto -> dto.setName(result.toString()));
    }

    private void calculateWeekRaidGold(Character character, CharacterResponse characterResponse, List<TodoResponseDto> todoResponseDtoList) {
        if (!character.getTodoV2List().isEmpty()) {
            GoldCheckPolicyEnum goldCheckPolicyEnum = character.getSettings().getGoldCheckPolicyEnum();
            if (character.isGoldCharacter()) {
                goldCheckPolicyEnum.getPolicy().calcTodoResponseDtoList(todoResponseDtoList);
            }
            todoResponseDtoList.sort(Comparator.comparingInt(TodoResponseDto::getSortNumber));

            character.getTodoV2List().stream()
                    .filter(TodoV2::isChecked)
                    .forEach(todo -> {
                        String weekCategory = todo.getWeekContent().getWeekCategory();

                        todoResponseDtoList.stream()
                                .filter(dto -> weekCategory.equals(dto.getWeekCategory()))
                                .forEach(dto -> processTodoForCharacterResponse(dto, todo, characterResponse));
                    });
        }
    }

    private void processTodoForCharacterResponse(TodoResponseDto todoResponseDto, TodoV2 todo, CharacterResponse characterResponse) {
        if (characterResponse.isGoldCharacter()) {
            handleGoldCharacter(todoResponseDto, todo, characterResponse);
        }
    }

    private void handleGoldCharacter(TodoResponseDto todoResponseDto, TodoV2 todo, CharacterResponse characterResponse) {
        if (todoResponseDto.isGoldCheck()) {
            int raidGold = todo.isMoreRewardCheck()
                    ? todo.getGold() - todo.getWeekContent().getMoreRewardGold()
                    : todo.getGold();
            characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() + raidGold);
        } else {
            if (todo.isMoreRewardCheck()) {
                characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() - todo.getWeekContent().getMoreRewardGold());
            }
        }
    }

    private void markCompletedTodos(List<TodoResponseDto> todoResponseDtoList) {
        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
            if (todoResponseDto.getCurrentGate() == todoResponseDto.getTotalGate()) {
                todoResponseDto.setCheck(true);
            }
        }
    }

    private void calculateBusGold(Character character, List<TodoResponseDto> todoResponseDtoList, CharacterResponse characterResponse) {
        Map<String, TodoResponseDto> categoryMap = todoResponseDtoList.stream()
                .collect(Collectors.toMap(TodoResponseDto::getWeekCategory, todo -> todo, (existing, replacement) -> existing));

        character.getRaidBusGoldList().forEach(gold -> {
            String weekCategory = gold.getWeekCategory();
            TodoResponseDto todo = categoryMap.get(weekCategory);
            if (todo != null) {
                todo.setRealGold(todo.getRealGold() + gold.getBusGold());
                if (todo.isCheck()) {
                    characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() + gold.getBusGold());
                }
            }
        });
    }
}
