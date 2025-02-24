package lostark.todo.domain.character.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lostark.todo.domain.util.cube.enums.CubeContentName;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpendWeekCubeRequest extends BaseCharacterRequest {

    @ApiModelProperty(notes = "큐브 컨텐츠 이름(변수명)")
    @NotNull
    private CubeContentName cubeContentName;
}
