package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateGeneralTodoItemRequest {

    @ApiModelProperty(example = "할 일 제목", required = true)
    @NotBlank
    @Size(max = 200)
    private String title;

    @ApiModelProperty(example = "할 일 상세 설명")
    private String description;

    @ApiModelProperty(example = "1", required = true)
    @NotNull
    private Long folderId;

    @ApiModelProperty(example = "1", required = true)
    @NotNull
    private Long categoryId;

    @ApiModelProperty(example = "2025-01-01T09:00", notes = "선택 사항")
    private String dueDate;

    @ApiModelProperty(example = "false")
    private Boolean completed;
}
