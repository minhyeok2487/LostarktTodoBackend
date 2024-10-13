package lostark.todo.domainV2.character.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidMessageRequest extends BaseCharacterRequest{

    @NotNull
    private long todoId;

    private String message;
}
