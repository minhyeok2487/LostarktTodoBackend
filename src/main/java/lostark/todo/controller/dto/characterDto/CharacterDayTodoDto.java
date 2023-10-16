package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.DayTodo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDayTodoDto {

    private long characterId;

    @NotEmpty()
    private String characterName;

    private Integer eponaCheck;

    private Integer eponaGauge;

    private Integer chaosGauge;

    private Integer chaosCheck;

    private Integer guardianGauge;

    private Integer guardianCheck;
}
