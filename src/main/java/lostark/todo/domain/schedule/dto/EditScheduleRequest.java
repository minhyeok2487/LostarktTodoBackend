package lostark.todo.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class EditScheduleRequest {

    @ApiModelProperty(example = "날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ApiModelProperty(example = "요일")
    private DayOfWeek dayOfWeek;

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    private LocalTime time;

    @ApiModelProperty(example = "메모")
    private String memo;
}
