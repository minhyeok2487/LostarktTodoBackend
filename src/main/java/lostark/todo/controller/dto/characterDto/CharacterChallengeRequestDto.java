package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.ChallengeContentEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterChallengeRequestDto {

    @ApiModelProperty(notes = "캐릭터 인덱스")
    private long characterId;

    @ApiModelProperty(notes = "서버 이름")
    private String serverName;

    @ApiModelProperty(notes = "콘텐츠 분류", value = "Guardian, Abyss")
    private ChallengeContentEnum content;
}
