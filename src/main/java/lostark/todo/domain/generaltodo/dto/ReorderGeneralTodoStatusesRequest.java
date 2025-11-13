package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReorderGeneralTodoStatusesRequest {

    @ApiModelProperty(example = "[10,11,12]", required = true)
    @NotEmpty
    private List<Long> statusIds;
}
