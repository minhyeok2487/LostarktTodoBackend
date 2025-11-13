package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateGeneralTodoItemStatusRequest {

    @ApiModelProperty(example = "7", required = true)
    @NotNull
    private Long statusId;
}
