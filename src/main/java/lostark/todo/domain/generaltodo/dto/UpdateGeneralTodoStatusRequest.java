package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateGeneralTodoStatusRequest {

    @ApiModelProperty(example = "검토 중", required = true)
    @NotBlank
    @Size(max = 50)
    private String name;
}
