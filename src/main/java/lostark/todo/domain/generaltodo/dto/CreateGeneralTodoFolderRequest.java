package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateGeneralTodoFolderRequest {

    @ApiModelProperty(example = "새 폴더 이름", required = true)
    @NotBlank
    @Size(max = 100)
    private String name;

    @ApiModelProperty(example = "0", notes = "선택 사항 - 지정하지 않으면 마지막으로 정렬됩니다.")
    private Integer sortOrder;
}
