package lostark.todo.domain.character.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidCheckRequest extends BaseCharacterRequest {

    @NotEmpty
    private String weekCategory;

    @NotNull
    private boolean allCheck;
}
