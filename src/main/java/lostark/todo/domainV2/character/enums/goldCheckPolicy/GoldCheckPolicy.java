package lostark.todo.domainV2.character.enums.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domainV2.character.entity.Character;

import java.util.List;

public interface GoldCheckPolicy {

    void calcTodoResponseDtoList(Character character, List<TodoResponseDto> todoResponseDtoList);
}
