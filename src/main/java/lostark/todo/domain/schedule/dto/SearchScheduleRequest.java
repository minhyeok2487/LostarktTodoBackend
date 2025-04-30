package lostark.todo.domain.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchScheduleRequest {

    @ApiModelProperty(example = "2024")
    @NotNull
    private int year;

    @ApiModelProperty(example = "1")
    @NotNull
    private int month;
}
