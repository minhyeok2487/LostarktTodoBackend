package lostark.todo.controller.dtoV2.character;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.util.content.enums.WeekContentCategory;

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

    @ApiModelProperty(notes = "캐릭터 메모")
    private String memo;

    public CharacterResponse toDto(Character character) {
        CharacterResponse characterResponse = CharacterResponse.builder()
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
                .build();

        List<TodoResponseDto> todoResponseDtoList = getTodoResponseDtos(character);

        //주간레이드 entity -> dto
        if (!character.getTodoV2List().isEmpty()) {
            // dtoList 만들기
            // goldCheckVersion별 따로
            GoldCheckPolicyEnum goldCheckPolicyEnum = character.getSettings().getGoldCheckPolicyEnum();
            goldCheckPolicyEnum.getPolicy().calcTodoResponseDtoList(todoResponseDtoList);

            //sortNumber 순서대로 출력
            todoResponseDtoList.sort(Comparator.comparingInt(TodoResponseDto::getSortNumber));

            // 캐릭터 주간 레이드 수익 저장
            if (characterResponse.isGoldCharacter()) {
                for (TodoV2 todo : character.getTodoV2List()) {
                    if (todo.isChecked()) {
                        String weekCategory = todo.getWeekContent().getWeekCategory();
                        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
                            if (weekCategory.equals(todoResponseDto.getWeekCategory()) && todoResponseDto.getRealGold() != 0) {
                                if (todo.isMoreRewardCheck()) {
                                    characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() + todo.getGold() - todo.getWeekContent().getMoreRewardGold());
                                } else {
                                    characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() + todo.getGold());
                                }
                            }
                        }
                    }
                }
            }
        }

        //버스 골드 계산
        character.getRaidBusGoldList().forEach(gold -> {
            characterResponse.setWeekRaidGold(characterResponse.getWeekRaidGold() + gold.getBusGold());
            todoResponseDtoList.stream()
                    .filter(todo -> todo.getWeekCategory().equals(gold.getWeekCategory()))
                    .findFirst()
                    .ifPresent(todoResponseDto -> todoResponseDto.setRealGold(todoResponseDto.getRealGold() + gold.getBusGold()));
        });

        //한 컨텐츠 완료 했는지 체크
        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
            if (todoResponseDto.getCurrentGate() == todoResponseDto.getTotalGate()) {
                todoResponseDto.setCheck(true);
            }
        }

        characterResponse.setTodoList(todoResponseDtoList);
        return characterResponse;
    }

    private List<TodoResponseDto> getTodoResponseDtos(Character character) {
        List<TodoResponseDto> todoResponseDtoList = createTodoResponseDtos(character);

        Map<String, Map<WeekContentCategory, List<Integer>>> categorizedMap = categorizeTodos(character);

        categorizedMap.forEach((key, value) -> buildResultString(key, value, todoResponseDtoList));
        return todoResponseDtoList;
    }

    private List<TodoResponseDto> createTodoResponseDtos(Character character) {
        List<TodoResponseDto> todoResponseDtos = new ArrayList<>();
        List<TodoV2> sortedTodos = getSortedTodos(character);
        sortedTodos.forEach(todo -> addOrUpdateTodoDto(character, todo, todoResponseDtos));
        return todoResponseDtos;
    }

    private List<TodoV2> getSortedTodos(Character character) {
        return character.getTodoV2List().stream()
                .filter(todo -> todo.getCoolTime() >= 1) //2주기 레이드 확인용
                .sorted(Comparator.comparingLong(todo -> todo.getWeekContent().getGate()))
                .collect(Collectors.toList());
    }

    private void addOrUpdateTodoDto(Character character, TodoV2 todo, List<TodoResponseDto> dtos) {
        TodoResponseDto existingDto = findExistingDto(todo, dtos);
        if (existingDto != null) {
            updateExistingDto(existingDto, todo);
            return;
        }
        dtos.add(new TodoResponseDto().toDto(todo, character.getSettings().isGoldCheckVersion()));
    }

    private TodoResponseDto findExistingDto(TodoV2 todo, List<TodoResponseDto> dtos) {
        return dtos.stream()
                .filter(dto -> dto.getWeekCategory().equals(todo.getWeekContent().getWeekCategory()))
                .findFirst()
                .orElse(null);
    }

    private void updateExistingDto(TodoResponseDto existingDto, TodoV2 todo) {
        existingDto.setGold(existingDto.getGold() + todo.getGold());
        existingDto.setRealGold(existingDto.getRealGold() + todo.getGold());
        existingDto.setTotalGate(todo.getWeekContent().getGate());
        updateCurrentGateIfChecked(existingDto, todo);
        existingDto.getMoreRewardCheckList().add(todo.isMoreRewardCheck());
        if(todo.isMoreRewardCheck()) {
            existingDto.setRealGold(existingDto.getRealGold() - todo.getWeekContent().getMoreRewardGold());
        }
    }

    private void updateCurrentGateIfChecked(TodoResponseDto dto, TodoV2 todo) {
        if (todo.isChecked()) {
            dto.setCurrentGate(todo.getWeekContent().getGate());
        }
    }

    private Map<String, Map<WeekContentCategory, List<Integer>>> categorizeTodos(Character character) {
        return character.getTodoV2List().stream()
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
}
