package lostark.todo.controller.dto.todoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {

    private String characterName;

    private long todoId;

    private boolean todoCheck;
}
