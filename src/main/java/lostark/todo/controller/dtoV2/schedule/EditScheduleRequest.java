package lostark.todo.controller.dtoV2.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class EditScheduleRequest {

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    @NotNull
    private LocalTime time;

    @ApiModelProperty(example = "메모")
    private String memo;
}
