package lostark.todo.controller.dtoV2.character;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterSortRequest {

    @NotEmpty()
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    private int sortNumber;
}
