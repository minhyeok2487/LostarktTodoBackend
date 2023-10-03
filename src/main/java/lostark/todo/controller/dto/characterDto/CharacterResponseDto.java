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
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todoV2.TodoV2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponseDto {

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
    private boolean eponaCheck; //에포나


    @ApiModelProperty(notes = "주간 숙제 체크")
    private List<TodoResponseDto> todoList;

    @ApiModelProperty(notes = "골드획득 지정")
    private boolean goldCharacter; //에포나

    private boolean challengeGuardian; //도전 가디언 토벌

    private boolean challengeAbyss; //도전 어비스 던전

    private Settings settings;

    public CharacterResponseDto toDto(Character character) {

        CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
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
                .eponaCheck(character.getDayTodo().isEponaCheck())
                .goldCharacter(character.isGoldCharacter())
                .challengeAbyss(character.isChallengeAbyss())
                .challengeGuardian(character.isChallengeGuardian())
                .settings(character.getSettings())
                .build();

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        if (!character.getTodoList().isEmpty()) {
            for (Todo todo : character.getTodoList()) {
                TodoResponseDto todoResponseDto = new TodoResponseDto().toDto(todo);
                todoResponseDtoList.add(todoResponseDto);
            }
        }

        characterResponseDto.setTodoList(todoResponseDtoList);

        return characterResponseDto;
    }

    public CharacterResponseDto toDtoV3(Character character) {

        CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
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
                .eponaCheck(character.getDayTodo().isEponaCheck())
                .goldCharacter(character.isGoldCharacter())
                .challengeAbyss(character.isChallengeAbyss())
                .challengeGuardian(character.isChallengeGuardian())
                .settings(character.getSettings())
                .build();

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();
        if(!character.getTodoV2List().isEmpty()){
            for (TodoV2 todo : character.getTodoV2List()) {
                boolean exitedCheck = false;
                for (TodoResponseDto exited : todoResponseDtoList) {
                    if (exited.getWeekCategory().equals(todo.getWeekContent().getWeekCategory())) {
                        exited.setName(exited.getName() + " " +todo.getWeekContent().getGate());
                        exited.setGold(exited.getGold()+todo.getWeekContent().getGold());
                        exitedCheck = true;
                        break;
                    }
                }
                if (!exitedCheck) {
                    todoResponseDtoList.add(new TodoResponseDto().toDto(todo));
                }
            }
        }
        characterResponseDto.setTodoList(todoResponseDtoList);

        return characterResponseDto;
    }
}
