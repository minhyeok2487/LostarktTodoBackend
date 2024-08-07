package lostark.todo.controller.dtoV2.character;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemoParams {

    @NotNull
    private long characterId;

    @Size(max = 100)
    private String memo;
}
