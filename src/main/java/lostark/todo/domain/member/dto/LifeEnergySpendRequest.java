package lostark.todo.domain.member.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LifeEnergySpendRequest {

    @NotNull
    private Long id;

    @Min(0)
    private int energy;

    @NotNull
    private int gold;

    @NotEmpty
    private String characterName;
}
