package lostark.todo.domain.util.cube.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.util.cube.entity.Cubes;

@Data
public class CubeResponse {

    private Long cubeId;

    private Long characterId;

    private String characterName;

    private double itemLevel;

    private int ban1;

    private int ban2;

    private int ban3;

    private int ban4;

    private int ban5;

    private int unlock1;

    private int unlock2;

    private int unlock3;

    @QueryProjection
    public CubeResponse(Character character, Cubes cubes) {
        this.cubeId = cubes.getId();
        this.characterId = character.getId();
        this.characterName = character.getCharacterName();
        this.itemLevel = character.getItemLevel();
        this.ban1 = cubes.getBan1();
        this.ban2 = cubes.getBan2();
        this.ban3 = cubes.getBan3();
        this.ban4 = cubes.getBan4();
        this.ban5 = cubes.getBan5();
        this.unlock1 = cubes.getUnlock1();
        this.unlock2 = cubes.getUnlock2();
        this.unlock3 = cubes.getUnlock3();
    }
}
