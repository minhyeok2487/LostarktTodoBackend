package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lostark.todo.domain.character.constants.ElysianConstants;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateElysianRequest extends BaseCharacterRequest {

    @NotNull
    private ElysianConstants.UpdateElysianActionEnum action;
}
