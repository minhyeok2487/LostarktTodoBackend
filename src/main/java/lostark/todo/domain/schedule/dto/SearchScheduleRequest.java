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

    @ApiModelProperty(example = "발탄", notes = "레이드 이름이나 메모에서 검색")
    private String query;
}
