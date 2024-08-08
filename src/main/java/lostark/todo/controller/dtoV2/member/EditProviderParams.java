package lostark.todo.controller.dtoV2.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class EditProviderParams {

    @NotEmpty
    @ApiModelProperty(example = "패스워드")
    private String password;
}