package lostark.todo.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;

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
