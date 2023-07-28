package lostark.todo.controller.v1.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDayContentResponseDto {

    @ApiModelProperty(example = "일일숙제 카오스던전 돌았는지 체크(0, 1, 2)")
    private int chaos;

    @ApiModelProperty(example = "카오스던전 휴식게이지")
    private int chaosGauge;

    @ApiModelProperty(example = "일일숙제 가디언토벌 돌았는지 체크(0, 1, 2)")
    private int guardian;

    @ApiModelProperty(example = "가디언토벌 휴식게이지")
    private int guardianGauge;
}
