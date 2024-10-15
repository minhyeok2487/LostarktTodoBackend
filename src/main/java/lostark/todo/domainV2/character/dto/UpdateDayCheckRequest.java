package lostark.todo.domainV2.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lostark.todo.domainV2.character.enums.DayTodoCategoryEnum;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDayCheckRequest extends BaseCharacterRequest {

    @NotNull
    private boolean allCheck;

    @NotNull
    private DayTodoCategoryEnum category;
}
