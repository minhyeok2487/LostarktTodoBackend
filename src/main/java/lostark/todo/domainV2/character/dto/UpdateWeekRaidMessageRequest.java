package lostark.todo.domainV2.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWeekRaidMessageRequest {

    @NotNull
    private long characterId;

    @NotNull
    private long todoId;

    private String message;
}
