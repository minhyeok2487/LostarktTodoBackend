package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReorderGeneralTodoStatusesRequest {

    @ApiModelProperty(example = "[21, 18, 35, 40]", required = true)
    @NotEmpty
    private List<Long> statusIds;
}
