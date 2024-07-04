package lostark.todo.controller.dtoV2.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class GetWeekScheduleRequest {

    @ApiModelProperty(example = "날짜(해당 날짜가 포함된 주의 스케줄을 가져옴. 단, 주간 반복 스케줄을 가져옴)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate date = LocalDate.now();
}
