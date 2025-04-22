package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class EditProviderRequest {

    @NotEmpty
    @ApiModelProperty(example = "패스워드")
    private String password;
}