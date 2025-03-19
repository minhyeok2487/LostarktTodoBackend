package lostark.todo.domain.character.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCharacterSettingRequest extends BaseCharacterRequest {

    private String characterName;

    private Object value;

    private String name;
}
