package lostark.todo.domain.character.enums.goldCheckPolicy;

import lostark.todo.domain.character.dto.TodoResponseDto;

import java.util.List;

public interface GoldCheckPolicy {

    void calcTodoResponseDtoList(List<TodoResponseDto> todoResponseDtoList);
}
