package lostark.todo.controller.v2.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDayContentDto {

    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @ApiModelProperty(notes = "카오스던전 숙제 할 캐릭인지 선택, true / false")
    private boolean chaosSelected;

    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private int chaosCheck;

    @ApiModelProperty(notes = "가디언토벌 숙제 할 캐릭인지 선택, true / false")
    private boolean guardianSelected;

    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 2, 1씩 증감")
    private int guardianCheck;
}
