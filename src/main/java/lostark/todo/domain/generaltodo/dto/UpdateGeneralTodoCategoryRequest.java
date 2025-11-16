package lostark.todo.domain.generaltodo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.generaltodo.enums.GeneralTodoViewMode;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdateGeneralTodoCategoryRequest {

    @ApiModelProperty(example = "새 카테고리 이름")
    @Size(max = 100)
    private String name;

    @ApiModelProperty(example = "#FFFFFF")
    @Pattern(regexp = "^(#([A-Fa-f0-9]{6}))?$", message = "색상은 #RRGGBB 형식이어야 합니다.")
    private String color;

    @ApiModelProperty(example = "TIMELINE", allowableValues = "LIST,KANBAN,TIMELINE")
    private GeneralTodoViewMode viewMode;

    public boolean isColorProvided() {
        return color != null;
    }

    public String getNormalizedColor() {
        return StringUtils.hasText(color) ? color : null;
    }
}
