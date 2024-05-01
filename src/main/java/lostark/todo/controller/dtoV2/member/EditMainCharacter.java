package lostark.todo.controller.dtoV2.member;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EditMainCharacter {

    @NotEmpty
    @ApiModelProperty(example = "변경할 캐릭터 이름")
    private String  mainCharacter;
}
