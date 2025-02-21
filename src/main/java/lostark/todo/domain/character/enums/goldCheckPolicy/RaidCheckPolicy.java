package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.domain.character.dto.TodoResponseDto;

import java.util.*;

public class RaidCheckPolicy implements GoldCheckPolicy {

    @Override
    public void calcTodoResponseDtoList(List<TodoResponseDto> todoResponseDtoList) {
        todoResponseDtoList.stream()
                .filter(todo -> !todo.isGoldCheck())
                .forEach(TodoResponseDto::calcRaidCheckPolicyGold);
    }
}
