package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateGeneralTodoStatusRequest {

    @ApiModelProperty(example = "진행중", required = true)
    @NotBlank
    @Size(max = 50)
    private String name;

    @ApiModelProperty(example = "1", notes = "선택 사항 - 지정하지 않으면 마지막으로 정렬됩니다.")
    private Integer sortOrder;
}
