package lostark.todo.controller.dtoV2.member;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MainCharacterResponse {

    @NotEmpty
    @ApiModelProperty(example = "캐릭터 tjqj")
    private String serverName;

    @NotEmpty
    @ApiModelProperty(example = "캐릭터 이름")
    private String characterName;

    @NotEmpty
    @ApiModelProperty(example = "캐릭터 이미지")
    private String characterImage;

    @NotEmpty
    @ApiModelProperty(example = "캐릭터 클래스")
    private String characterClassName;

    @NotEmpty
    @ApiModelProperty(example = "아이템 레벨")
    private double itemLevel;

    @QueryProjection
    public MainCharacterResponse(String serverName, String characterName, String characterImage, String characterClassName, double itemLevel) {
        this.serverName = serverName;
        this.characterName = characterName;
        this.characterImage = characterImage;
        this.characterClassName = characterClassName;
        this.itemLevel = itemLevel;
    }
}
