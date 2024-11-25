package lostark.todo.domainV2.character.enums.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.entity.TodoV2;

import java.util.Comparator;
import java.util.List;

public class TopThreePolicy implements GoldCheckPolicy {

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
                    TodoResponseDto dto = new TodoResponseDto().toDto(todo, character.getSettings().isGoldCheckVersion());
                    todoResponseDtoList.add(dto);
                }
            }
        }
        maxThree(todoResponseDtoList);
    }

    private void maxThree(List<TodoResponseDto> todoResponseDtoList) {
        todoResponseDtoList.sort(Comparator.comparing(TodoResponseDto::getGold).reversed());
        for (int i = 0; i < todoResponseDtoList.size(); i++) {
            TodoResponseDto todoResponseDto = todoResponseDtoList.get(i);
            if (i >= 3) {
                todoResponseDto.setGold(0);
            }
        }
    }
}
