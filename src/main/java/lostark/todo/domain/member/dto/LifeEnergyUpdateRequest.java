package lostark.todo.domain.member.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LifeEnergyUpdateRequest {

    @NotNull
    private Long id;

    @Min(0)
    private int energy;

    @Min(0)
    private int maxEnergy;

    @NotEmpty
    private String characterName;

    private boolean beatrice;
}
