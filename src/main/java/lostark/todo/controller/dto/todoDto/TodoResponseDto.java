package lostark.todo.controller.dto.todoDto;

import lombok.*;
import lostark.todo.domain.util.content.enums.WeekContentCategory;
import lostark.todo.domain.character.entity.TodoV2;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TodoResponseDto {

    private long id;

    private String name;

    private String characterClassName;

    private int gold;

    private int realGold;

    private boolean check;

    private String message;

    private int currentGate;

    private int totalGate;

    private String weekCategory;

    private WeekContentCategory weekContentCategory;

    private int sortNumber;

    private boolean goldCheck;

    private List<Boolean> moreRewardCheckList;

    public TodoResponseDto toDto(TodoV2 todo, boolean goldCheckVersion) {
        TodoResponseDto build = TodoResponseDto.builder()
                .id(todo.getId())
                .check(false)
                .name("")
                .gold(todo.getGold())
                .realGold(calcRealGold(todo))
                .message(todo.getMessage())
                .currentGate(todo.isChecked() ? todo.getWeekContent().getGate() : 0)
                .totalGate(todo.getWeekContent().getGate())
                .weekCategory(todo.getWeekContent().getWeekCategory())
                .weekContentCategory(todo.getWeekContent().getWeekContentCategory())
                .sortNumber(todo.getSortNumber())
                .goldCheck(todo.isGoldCheck())
                .characterClassName(todo.getCharacter().getCharacterClassName())
                .moreRewardCheckList(new ArrayList<>(Collections.singleton(todo.isMoreRewardCheck())))
                .build();

        if(goldCheckVersion && !todo.isGoldCheck()) {
            build.setGold(0);
        }
        return build;
    }

    private int calcRealGold(TodoV2 todo) {
        if(todo.isMoreRewardCheck()) {
            return todo.getGold() - todo.getWeekContent().getMoreRewardGold();
        } else {
            return todo.getGold();
        }
    }
}
