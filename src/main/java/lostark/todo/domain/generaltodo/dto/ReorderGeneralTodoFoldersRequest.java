package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReorderGeneralTodoFoldersRequest {

    @ApiModelProperty(example = "[1,2,3]", required = true)
    @NotEmpty
    private List<Long> folderIds;
}
