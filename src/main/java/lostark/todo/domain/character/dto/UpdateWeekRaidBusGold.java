package lostark.todo.domain.character.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidBusGold extends BaseCharacterRequest {

    @NotEmpty
    private String weekCategory;

    @NotEmpty
    private int busGold;
}