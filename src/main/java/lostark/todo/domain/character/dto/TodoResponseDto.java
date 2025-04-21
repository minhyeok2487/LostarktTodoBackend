package lostark.todo.domain.character.dto;

import lombok.*;
import lostark.todo.domain.content.enums.WeekContentCategory;
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

    private List<Integer> moreRewardGoldList;

    public TodoResponseDto toDto(TodoV2 todo, boolean goldCharacter) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .check(false)
                .name("")
                .gold(todo.getGold())
                .realGold(calcRealGold(todo, goldCharacter))
                .message(todo.getMessage())
                .currentGate(todo.isChecked() ? todo.getWeekContent().getGate() : 0)
                .totalGate(todo.getWeekContent().getGate())
                .weekCategory(todo.getWeekContent().getWeekCategory())
                .weekContentCategory(todo.getWeekContent().getWeekContentCategory())
                .sortNumber(todo.getSortNumber())
                .goldCheck(todo.isGoldCheck())
                .characterClassName(todo.getCharacter().getCharacterClassName())
                .moreRewardCheckList(new ArrayList<>(Collections.singleton(todo.isMoreRewardCheck())))
                .moreRewardGoldList(new ArrayList<>(Collections.singleton(todo.getWeekContent().getMoreRewardGold())))
                .build();
    }

    private int calcRealGold(TodoV2 todo, boolean goldCharacter) {
        int baseGold = todo.getGold();
        int moreRewardGold = todo.getWeekContent().getMoreRewardGold();

        if (todo.isMoreRewardCheck()) {
            return goldCharacter ? baseGold - moreRewardGold : 0;
        }

        return goldCharacter ? baseGold : 0;
    }


    public void calcRaidCheckPolicyGold() {
        this.realGold = this.getRealGold() - this.getGold();
    }

    public void updateExistingTodo(TodoV2 todo, boolean goldCharacter) {
        this.gold = this.getGold() + todo.getGold();
        this.totalGate = todo.getWeekContent().getGate();
        this.moreRewardCheckList.add(todo.isMoreRewardCheck());
        this.moreRewardGoldList.add(todo.getWeekContent().getMoreRewardGold());

        int moreRewardGold = todo.getWeekContent().getMoreRewardGold();
        int goldToAdd = goldCharacter ? todo.getGold() : 0;

        if (todo.isMoreRewardCheck() && goldCharacter) {
            goldToAdd -= moreRewardGold;
        }

        setRealGold(getRealGold() + goldToAdd);
        updateCurrentGateIfChecked(todo);
    }

    public void updateCurrentGateIfChecked(TodoV2 todo) {
        if (todo.isChecked()) {
            setCurrentGate(todo.getWeekContent().getGate());
        }
    }
}
