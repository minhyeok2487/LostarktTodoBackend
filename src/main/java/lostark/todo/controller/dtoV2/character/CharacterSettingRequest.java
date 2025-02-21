package lostark.todo.controller.dtoV2.character;

import lombok.*;
import lostark.todo.domain.character.dto.BaseCharacterRequest;

@EqualsAndHashCode(callSuper = true)
@Data
public class CharacterSettingRequest extends BaseCharacterRequest {

    private String characterName;

    private Object value;

    private String name;
}
