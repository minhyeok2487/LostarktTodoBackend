package lostark.todo.domain.util.cube.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.character.entity.Character;

@Data
public class SpendCubeResponse {

    private Long characterId;

    private String characterName;

    private double itemLevel;

    private String name;

    private int profit;

    @QueryProjection
    public SpendCubeResponse(Character character, String name, double profit) {
        this.characterId = character.getId();
        this.characterName = character.getCharacterName();
        this.itemLevel = character.getItemLevel();
        this.name = name;
        this.profit = (int) profit;
    }
}
