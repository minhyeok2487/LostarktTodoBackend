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
public class CharacterDayTodoDto {

    @NotEmpty()
    private String characterName;

    private boolean eponaCheck;

    @Min(value = 0)
    @Max(value = 100)
    private Integer chaosGauge;

    private Integer chaosCheck;

    @Min(value = 0)
    @Max(value = 100)
    private Integer guardianGauge;

    private Integer guardianCheck;
}
