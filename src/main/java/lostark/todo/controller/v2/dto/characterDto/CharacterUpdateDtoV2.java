package lostark.todo.controller.v2.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterUpdateDtoV2 {

    @NotBlank(message = "캐릭터 이름이 없습니다")
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @NotNull(message = "chaosSelected: null일 수 없습니다.")
    @ApiModelProperty(notes = "카오스던전 숙제 할 캐릭인지 선택, true / false")
    private Boolean chaosSelected;

    @Min(value = 0, message = "chaosCheck: 0~2 사이여야 합니다.")
    @Max(value = 2, message = "chaosCheck: 0~2 사이여야 합니다.")
    @NotNull(message = "chaosCheck: null일 수 없습니다.")
    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private Integer chaosCheck;

    @NotNull(message = "guardianSelected: null일 수 없습니다.")
    @ApiModelProperty(notes = "가디언토벌 숙제 할 캐릭인지 선택, true / false")
    private Boolean guardianSelected;

    @Min(value = 0, message = "guardianCheck : 0~2 사이여야 합니다.")
    @Max(value = 2, message = "guardianCheck : 0~2 사이여야 합니다.")
    @NotNull(message = "guardianCheck: null일 수 없습니다.")
    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 2, 1씩 증감")
    private Integer guardianCheck;

}
