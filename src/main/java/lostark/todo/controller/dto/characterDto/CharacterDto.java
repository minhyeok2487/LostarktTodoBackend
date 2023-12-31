package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.Settings;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.todoV2.TodoV2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDto {

    private long id;

    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemLevel;

    private String serverName;

    private int sortNumber;

    @ApiModelProperty(notes = "카오스던전 컨텐츠 이름")
    private String chaosName;

    private DayContent chaos;

    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private int chaosCheck;

    @ApiModelProperty(notes = "카오스던전 휴식게이지, 최소 0, 최대 100, 10단위 증가")
    private int chaosGauge;

    @ApiModelProperty(notes = "카오스던전 숙제 완료 시 예상 수익")
    private double chaosGold;

    @ApiModelProperty(notes = "가디언토벌 컨텐츠 내용")
    private String guardianName;

    private DayContent guardian;

    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 1, 1씩 증감")
    private int guardianCheck;

    @ApiModelProperty(notes = "가디언토벌 휴식게이지, 최소 0, 최대 100, 10씩 증감")
    private int guardianGauge;

    @ApiModelProperty(notes = "가디언토벌 숙제 완료 시 예상 수익")
    private double guardianGold;

    @ApiModelProperty(notes = "에포나 의뢰 체크")
    private int eponaCheck; //에포나

    private int eponaGauge;

    @ApiModelProperty(notes = "주간 숙제 체크")
    private List<TodoResponseDto> todoList;

    @ApiModelProperty(notes = "골드획득 지정")
    private boolean goldCharacter; //에포나

    private boolean challengeGuardian; //도전 가디언 토벌

    private boolean challengeAbyss; //도전 어비스 던전

    private Settings settings;

    private int weekGold;

    private int weekEpona;

    private boolean silmaelChange;

    private int cubeTicket;

    public CharacterDto toDtoV2(Character character) {

        CharacterDto characterDto = CharacterDto.builder()
                .id(character.getId())
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
                .build();

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        //weekContent gate 순으로 정렬
        if(!character.getTodoV2List().isEmpty()){
            character.getTodoV2List().sort(Comparator.comparingLong(TodoV2 -> TodoV2.getWeekContent().getGate()));
            for (TodoV2 todo : character.getTodoV2List()) {
                if(todo.getCoolTime()>=1) {
                    boolean exitedCheck = false;
                    for (TodoResponseDto exited : todoResponseDtoList) {
                        if (exited.getWeekCategory().equals(todo.getWeekContent().getWeekCategory())) {
                            if (exited.getWeekContentCategory().equals(todo.getWeekContent().getWeekContentCategory())) {
                                exited.setName(exited.getName() + " " +todo.getWeekContent().getGate());
                            } else {
                                if (exited.getName().contains("하드") && exited.getName().contains("노말")) {
                                    exited.setName(exited.getName() + " " + " "+todo.getWeekContent().getGate());
                                } else {
                                    exited.setName(exited.getName() + " " + todo.getWeekContent().getWeekContentCategory()+ " " +todo.getWeekContent().getGate());
                                }
                            }
                            exited.setGold(exited.getGold()+todo.getWeekContent().getGold());
                            exited.setTotalGate(todo.getWeekContent().getGate());
                            if(todo.isChecked()) {
                                exited.setCurrentGate(todo.getWeekContent().getGate());
                            }
                            exitedCheck = true;
                            break;
                        }
                    }
                    if (!exitedCheck) {
                        TodoResponseDto dto = new TodoResponseDto().toDto(todo);
                        todoResponseDtoList.add(dto);
                    }
                }

            }

            //골드 획득 - 높은 순으로 3개
            maxThree(todoResponseDtoList);

            // 캐릭터 주간 레이드 수익 저장
            if (characterDto.isGoldCharacter()) {
                for (TodoV2 todo : character.getTodoV2List()) {
                    if (todo.isChecked()) {
                        String weekCategory = todo.getWeekContent().getWeekCategory();
                        int todoGold = todo.getGold();
                        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
                            if (weekCategory.equals(todoResponseDto.getWeekCategory()) && todoResponseDto.getGold() != 0) {
                                characterDto.setWeekGold(characterDto.getWeekGold() + todoGold);
                            }
                        }
                    }
                }
            }
        }

        //한 컨텐츠 완료 했는지 체크
        for (TodoResponseDto todoResponseDto : todoResponseDtoList) {
            if (todoResponseDto.getCurrentGate() == todoResponseDto.getTotalGate()) {
                todoResponseDto.setCheck(true);
            }
        }

        characterDto.setTodoList(todoResponseDtoList);

        return characterDto;
    }

    private static void maxThree(List<TodoResponseDto> todoResponseDtoList) {
        todoResponseDtoList.sort(Comparator.comparing(TodoResponseDto::getGold).reversed());
        for (int i = 0; i < todoResponseDtoList.size(); i++) {
            TodoResponseDto todoResponseDto = todoResponseDtoList.get(i);
            if (i >= 3) {
                todoResponseDto.setGold(0);
            }
        }
    }

    public List<CharacterDto> toDtoList(List<Character> characterList) {
        List<CharacterDto> characterDtoList = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterDtoList.sort(Comparator
                .comparingInt(CharacterDto::getSortNumber)
                .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed())
        );
        return characterDtoList;
    }
}
