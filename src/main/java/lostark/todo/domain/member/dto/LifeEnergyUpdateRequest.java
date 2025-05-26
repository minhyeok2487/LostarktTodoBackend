package lostark.todo.domain.member.dto;

import lombok.Data;

@Data
public class LifeEnergyUpdateRequest {

    private Long id;

    private int energy;

    private int maxEnergy;

    private String characterName;

    private boolean beatrice;
}
