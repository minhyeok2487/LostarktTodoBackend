package lostark.todo.controller.dto.characterDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

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
