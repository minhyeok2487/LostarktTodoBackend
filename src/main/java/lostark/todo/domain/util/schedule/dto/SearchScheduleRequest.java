package lostark.todo.domain.util.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchScheduleRequest {

    @ApiModelProperty(example = "월")
    @NotNull
    private int month;
}
