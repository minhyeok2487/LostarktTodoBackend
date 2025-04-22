package lostark.todo.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckCustomTodoRequest {

    @NotNull
    private long characterId;

    @NotNull
    private long customTodoId;
}
