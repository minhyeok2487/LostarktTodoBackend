package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.generaltodo.enums.GeneralTodoViewMode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateGeneralTodoCategoryRequest {

    @ApiModelProperty(example = "새 카테고리 이름", required = true)
    @NotBlank
    @Size(max = 100)
    private String name;

    @ApiModelProperty(example = "#FFFFFF", notes = "선택 사항")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "색상은 #RRGGBB 형식이어야 합니다.")
    private String color;

    @ApiModelProperty(example = "0", notes = "선택 사항 - 지정하지 않으면 마지막으로 정렬됩니다.")
    private Integer sortOrder;

    @ApiModelProperty(example = "LIST", allowableValues = "LIST,KANBAN", notes = "선택 사항 - 기본값 LIST")
    private GeneralTodoViewMode viewMode;
}
