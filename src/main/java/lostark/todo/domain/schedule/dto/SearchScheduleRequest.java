package lostark.todo.domain.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchScheduleRequest {

    @ApiModelProperty(example = "년")
    @NotNull
    private int year;

    @ApiModelProperty(example = "월")
    @NotNull
    private int month;
}
