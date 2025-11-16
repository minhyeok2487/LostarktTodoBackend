package lostark.todo.domain.generaltodo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ApiModelProperty(example = "2025-01-01T09:00")
    private String startDate;

    @ApiModelProperty(example = "2025-01-02T12:00")
    private String dueDate;

    @ApiModelProperty(example = "true")
    @JsonProperty("isAllDay")
    private Boolean isAllDay;

    @ApiModelProperty(example = "6")
    private Long statusId;

    public boolean scheduleFieldsProvided() {
        return startDate != null || dueDate != null || isAllDay != null;
    }
}
