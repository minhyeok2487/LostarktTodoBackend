package lostark.todo.controller.dto.characterDto;

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
public class CharacterUpdateDto {

    @NotEmpty()
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @NotNull()
    @ApiModelProperty(notes = "카오스던전 숙제 할 캐릭인지 선택, true / false")
    private Boolean chaosSelected;

    @Min(value = 0)
    @Max(value = 2)
    @NotNull()
    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private Integer chaosCheck;

    @NotNull()
    @ApiModelProperty(notes = "가디언토벌 숙제 할 캐릭인지 선택, true / false")
    private Boolean guardianSelected;

    @Min(value = 0)
    @Max(value = 1)
    @NotNull()
    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 1, 1씩 증감")
    private Integer guardianCheck;

}
