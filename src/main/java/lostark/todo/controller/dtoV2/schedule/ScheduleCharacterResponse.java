package lostark.todo.controller.dtoV2.schedule;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScheduleCharacterResponse {

    @ApiModelProperty(example = "캐릭터 Id")
    private long characterId;

    @ApiModelProperty(example = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(example = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(example = "캐릭터 레벨")
    private double itemLevel;

    @ApiModelProperty(example = "캐릭터 이미지 url")
    private String characterImage;

    @QueryProjection
    public ScheduleCharacterResponse(long characterId, String characterName, String characterClassName, double itemLevel, String characterImage) {
        this.characterId = characterId;
        this.characterName = characterName;
        this.characterClassName = characterClassName;
        this.itemLevel = itemLevel;
        this.characterImage = characterImage;
    }
}
