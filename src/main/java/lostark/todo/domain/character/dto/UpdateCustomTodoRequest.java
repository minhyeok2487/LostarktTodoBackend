package lostark.todo.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomTodoRequest {

    @NotNull
    private long characterId;

    @NotEmpty
    @Size(max = 20)
    private String contentName;
}
