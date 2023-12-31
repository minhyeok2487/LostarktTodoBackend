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

    private long characterId;

    private String characterName;

    private long todoId;

    private boolean todoCheck;

    private String message;

    private String weekCategory;

    private int currentGate;

    private int totalGate;
}
