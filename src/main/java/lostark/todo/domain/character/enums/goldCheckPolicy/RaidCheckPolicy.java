package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.TodoV2;

import java.util.*;

public class RaidCheckPolicy implements GoldCheckPolicy {
    @Override
    public void calcTodoResponseDtoList(Character character, List<TodoResponseDto> todoResponseDtoList) {
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
                        if (exited.isGoldCheck()) {
                            exited.setGold(exited.getGold()+todo.getWeekContent().getGold());
                        }
                        exited.setTotalGate(todo.getWeekContent().getGate());
                        if(todo.isChecked()) {
                            exited.setCurrentGate(todo.getWeekContent().getGate());
                        }
                        exitedCheck = true;

                        //더보기 리스트 업데이트
                        exited.getMoreRewardCheckList().add(todo.isMoreRewardCheck());
                        break;
                    }
                }
                if (!exitedCheck) {
                    TodoResponseDto dto = new TodoResponseDto().toDto(todo, character.getSettings().isGoldCheckVersion());
                    todoResponseDtoList.add(dto);
                }
            }
        }
    }
}
