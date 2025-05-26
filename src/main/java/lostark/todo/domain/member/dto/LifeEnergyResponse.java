package lostark.todo.domain.member.dto;

import lombok.Data;
import lostark.todo.domain.member.entity.LifeEnergy;

import java.util.List;

@Data
public class LifeEnergyResponse {

    private Long lifeEnergyId;

    private int energy;

    private int maxEnergy;

    private String characterName;

    private boolean beatrice;

    public LifeEnergyResponse(LifeEnergy it) {
        this.lifeEnergyId = it.getId();
        this.energy = it.getEnergy();
        this.maxEnergy = it.getMaxEnergy();
        this.characterName = it.getCharacterName();
        this.beatrice = it.isBeatrice();
    }

    public static List<LifeEnergyResponse> toDto(List<LifeEnergy> lifeEnergyList) {
        return lifeEnergyList.stream().map(LifeEnergyResponse::new).toList();
    }
}
