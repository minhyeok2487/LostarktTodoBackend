package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateGeneralTodoItemRequest {

    @ApiModelProperty(example = "새 할 일 제목")
    @Size(max = 200)
    private String title;

    @ApiModelProperty(example = "새 할 일 상세 설명")
    private String description;

    @ApiModelProperty(example = "2")
    private Long folderId;

    @ApiModelProperty(example = "3")
    private Long categoryId;

    @ApiModelProperty(example = "2025-01-02T12:00")
    private String dueDate;

    @ApiModelProperty(example = "true")
    private Boolean completed;

    @ApiModelProperty(example = "12")
    private Long statusId;
}
