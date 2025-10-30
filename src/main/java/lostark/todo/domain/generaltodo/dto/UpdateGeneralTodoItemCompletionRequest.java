package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateGeneralTodoItemCompletionRequest {

    @ApiModelProperty(example = "true", required = true)
    @NotNull
    private Boolean completed;
}
