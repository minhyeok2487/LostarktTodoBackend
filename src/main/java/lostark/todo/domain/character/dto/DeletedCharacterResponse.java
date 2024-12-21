package lostark.todo.domain.character.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeletedCharacterResponse {

    private long characterId;

    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(notes = "서버 이름")
    private String serverName;

    @QueryProjection
    public DeletedCharacterResponse(long characterId, String characterClassName, String characterImage, String characterName, double itemLevel, String serverName) {
        this.characterId = characterId;
        this.characterClassName = characterClassName;
        this.characterImage = characterImage;
        this.characterName = characterName;
        this.itemLevel = itemLevel;
        this.serverName = serverName;
    }
}
