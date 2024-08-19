package lostark.todo.controller.dtoV2.character;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.customTodo.CustomTodoFrequencyEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomTodoRequest {

    @NotNull
    private long characterId;

    @NotEmpty
    @Size(max = 20)
    private String contentName;

    @NotNull
    private CustomTodoFrequencyEnum frequency;
}
