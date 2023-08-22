package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterGaugeDto {

    @NotEmpty()
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @Min(value = 0)
    @Max(value = 100)
    @NotNull()
    @ApiModelProperty(notes = "")
    private Integer chaosGauge;

    @Min(value = 0)
    @Max(value = 100)
    @NotNull()
    @ApiModelProperty(notes = "")
    private Integer guardianGauge;

}
