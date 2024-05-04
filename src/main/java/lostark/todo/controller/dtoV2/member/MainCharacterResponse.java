package lostark.todo.controller.dtoV2.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.Character;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
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

    public MainCharacterResponse(Character character) {
        this.serverName = character.getServerName();
        this.characterName = character.getCharacterName();
        this.characterImage = character.getCharacterImage();
        this.characterClassName = character.getCharacterClassName();
        this.itemLevel = character.getItemLevel();
    }
}
