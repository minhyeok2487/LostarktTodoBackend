package lostark.todo.domain.cube.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.character.entity.Character;

@Data
public class SpendCubeResponse {

    private Long characterId;

    private Long memberId;

    private String characterName;

    private String serverName;

    private double itemLevel;

    private String name;

    private int profit;

    @QueryProjection
    public SpendCubeResponse(Character character, String name, double profit) {
        this.characterId = character.getId();
        this.memberId = character.getMember().getId();
        this.characterName = character.getCharacterName();
        this.serverName = character.getServerName();
        this.itemLevel = character.getItemLevel();
        this.name = name;
        this.profit = (int) profit;
    }
}
