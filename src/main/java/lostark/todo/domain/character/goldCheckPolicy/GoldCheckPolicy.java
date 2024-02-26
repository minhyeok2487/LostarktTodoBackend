package lostark.todo.domain.character.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;

import java.util.List;

public interface GoldCheckPolicy {

    void calcTodoResponseDtoList(Character character, List<TodoResponseDto> todoResponseDtoList);
}
