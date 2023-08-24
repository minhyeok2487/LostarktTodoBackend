package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;

import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterCheckDto {

    @NotEmpty()
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @Min(value = 0)
    @Max(value = 2)
    @NotNull()
    @ApiModelProperty(notes = "카오스던전 돌았는지 체크, 최소 0, 최대 2")
    private Integer chaosCheck;

    @Min(value = 0)
    @Max(value = 1)
    @NotNull()
    @ApiModelProperty(notes = "가디언토벌 돌았는지 체크, 최소 0, 최대 1, 1씩 증감")
    private Integer guardianCheck;

    @Min(value = 0)
    @Max(value = 3)
    @NotNull()
    @ApiModelProperty(notes = "에포나의뢰 체크, 최소 0, 최대 3")
    private Integer eponaCheck;

}
