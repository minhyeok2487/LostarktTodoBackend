package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.controller.dto.todoDto.TodoResponseDto;

import java.util.List;

public interface GoldCheckPolicy {

    void calcTodoResponseDtoList(List<TodoResponseDto> todoResponseDtoList);
}
