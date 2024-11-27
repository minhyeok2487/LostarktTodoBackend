package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDayCheckRequest extends BaseCharacterRequest {

    @NotNull
    private boolean allCheck;

    @NotNull
    private DayTodoCategoryEnum category;
}
