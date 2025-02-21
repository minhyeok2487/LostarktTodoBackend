package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.domain.character.dto.TodoResponseDto;

import java.util.Comparator;
import java.util.List;

public class TopThreePolicy implements GoldCheckPolicy {

    @Override
    public void calcTodoResponseDtoList(List<TodoResponseDto> todoResponseDtoList) {
        todoResponseDtoList.sort(Comparator.comparing(TodoResponseDto::getGold).reversed());
        for (int i = 0; i < todoResponseDtoList.size(); i++) {
            TodoResponseDto todoResponseDto = todoResponseDtoList.get(i);
            if (i >= 3) {
                todoResponseDto.calcRaidCheckPolicyGold();
                todoResponseDto.setGoldCheck(false);
            } else {
                todoResponseDto.setGoldCheck(true);
            }
        }
    }
}
