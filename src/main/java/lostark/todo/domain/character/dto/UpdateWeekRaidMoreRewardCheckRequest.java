package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidMoreRewardCheckRequest extends BaseCharacterRequest {

    @NotEmpty
    private String weekCategory;

    @NotEmpty
    private int gate;
}