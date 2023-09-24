package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.todo.Todo;

import javax.validation.constraints.Size;
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

    public CharacterResponseDto createResponseDto(Character character) {

        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : character.getTodoList()) {
            TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                    .id(todo.getId())
                    .check(todo.isChecked())
                    .contentName(todo.getContentName())
                    .name(todo.getName())
                    .gold(todo.getGold())
                    .message(todo.getMessage())
                    .build();
            todoResponseDtoList.add(todoResponseDto);
        }
        // 골드 획득 내림차순으로 정렬
        List<TodoResponseDto> sortedTodoList = todoResponseDtoList.stream()
                .sorted(Comparator.comparing(TodoResponseDto::getGold).reversed()).collect(Collectors.toList());
        for (int i = 0; i < sortedTodoList.size(); i++) {
            TodoResponseDto todoResponseDto = sortedTodoList.get(i);
            if (i >= 3) {
                todoResponseDto.setGold(0);
            }
        }

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
                .todoList(sortedTodoList)
                .goldCharacter(character.isGoldCharacter())
                .build();
        return characterResponseDto;
    }
}
