package lostark.todo.domainV2.util.cube.dto;

import lombok.Data;

@Data
public class CubeUpdateRequest {

    private Long cubeId;

    private Long characterId;

    private int ban1;

    private int ban2;

    private int ban3;

    private int ban4;

    private int ban5;

    private int unlock1;

    private int unlock2;
}
