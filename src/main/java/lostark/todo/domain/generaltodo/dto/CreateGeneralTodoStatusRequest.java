package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateGeneralTodoStatusRequest {

    @ApiModelProperty(example = "요청 중", required = true)
    @NotBlank
    @Size(max = 100)
    private String name;
}
