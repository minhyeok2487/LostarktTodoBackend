package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekEponaRequest extends BaseCharacterRequest {

    private boolean allCheck;
}
