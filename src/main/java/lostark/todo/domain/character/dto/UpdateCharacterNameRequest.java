package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCharacterNameRequest extends BaseCharacterRequest{

    @NotEmpty
    private String characterName;
}