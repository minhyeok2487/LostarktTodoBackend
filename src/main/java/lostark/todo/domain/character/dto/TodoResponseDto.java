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

    private int characterGold; //캐릭터 귀속 골드

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
                .characterGold(calcCharacterGold(todo, goldCharacter))
                .build();
    }

    private int calcRealGold(TodoV2 todo, boolean goldCharacter) {
        int baseGold = todo.getGold();
        int moreRewardGold = todo.getWeekContent().getMoreRewardGold();

        if (todo.isMoreRewardCheck() && todo.getCharacterGold() == 0) {
            return goldCharacter ? baseGold - moreRewardGold : 0;
        }

        return goldCharacter ? baseGold : 0;
    }

    private int calcCharacterGold(TodoV2 todo, boolean goldCharacter) {
        int baseGold = todo.getCharacterGold();
        int moreRewardGold = todo.getWeekContent().getMoreRewardGold();

        if (todo.isMoreRewardCheck() && todo.getCharacterGold() != 0) {
            return goldCharacter ? baseGold - moreRewardGold : 0;
        }

        return goldCharacter ? baseGold : 0;
    }


    public void calcRaidCheckPolicyGold() {
        this.realGold = this.getRealGold() - this.getGold();
    }

    public void updateExistingTodo(TodoV2 todo, boolean goldCharacter) {
        int todoGold = todo.getGold();
        int todoCharacterGold = todo.getCharacterGold();
        int moreRewardGold = todo.getWeekContent().getMoreRewardGold();
        boolean isMoreRewardChecked = todo.isMoreRewardCheck();

        // 공통 처리
        this.gold += todoGold;
        this.totalGate = todo.getWeekContent().getGate();
        this.moreRewardCheckList.add(isMoreRewardChecked);
        this.moreRewardGoldList.add(moreRewardGold);

        int realGoldToAdd = 0;
        int characterGoldToAdd = 0;

        if (todoCharacterGold == 0) {
            if (goldCharacter) {
                realGoldToAdd = todoGold;
                if (isMoreRewardChecked) {
                    realGoldToAdd -= moreRewardGold;
                }
            }
        } else {
            if (goldCharacter) {
                realGoldToAdd = todoGold;
                characterGoldToAdd = todoCharacterGold;
                if (isMoreRewardChecked) {
                    characterGoldToAdd -= moreRewardGold;
                }
            }
        }

        setRealGold(getRealGold() + realGoldToAdd);
        setCharacterGold(getCharacterGold() + characterGoldToAdd);

        updateCurrentGateIfChecked(todo);
    }


    public void updateCurrentGateIfChecked(TodoV2 todo) {
        if (todo.isChecked()) {
            setCurrentGate(todo.getWeekContent().getGate());
        }
    }
}
