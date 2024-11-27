package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.entity.Character;

import java.util.List;

public interface GoldCheckPolicy {

    void calcTodoResponseDtoList(Character character, List<TodoResponseDto> todoResponseDtoList);
}
