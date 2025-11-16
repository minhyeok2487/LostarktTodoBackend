package lostark.todo.domain.generaltodo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ApiModelProperty(example = "2025-01-01T09:00", notes = "타임라인 뷰에서만 사용됩니다.")
    private String startDate;

    @ApiModelProperty(example = "2025-01-01T09:00", notes = "선택 사항")
    private String dueDate;

    @ApiModelProperty(example = "true", notes = "리스트/칸반 뷰에서 하루종일 일정을 설정합니다.")
    @JsonProperty("isAllDay")
    private Boolean isAllDay;

    @ApiModelProperty(example = "5", notes = "선택 사항 - 지정하지 않으면 기본 상태로 지정됩니다.")
    private Long statusId;
}
