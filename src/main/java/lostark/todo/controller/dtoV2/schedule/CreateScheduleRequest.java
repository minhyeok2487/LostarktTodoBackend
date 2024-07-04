package lostark.todo.controller.dtoV2.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRaidCategory;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateScheduleRequest {

    @ApiModelProperty(example = "일정 종류")
    @NotNull
    private ScheduleCategory scheduleCategory;

    @ApiModelProperty(example = "레이드 카테고리")
    @NotNull
    private ScheduleRaidCategory scheduleRaidCategory;

    @ApiModelProperty(example = "레이드 이름")
    private String raidName;

    @ApiModelProperty(example = "레이드 레벨")
    private String raidLevel;

    @ApiModelProperty(example = "요일 / 1(월요일) - 7(일요일)")
    @NotNull
    private DayOfWeek dayOfWeek;

    @ApiModelProperty(example = "일정 시간 (10분 단위) ex) 19:00")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime time;

    @ApiModelProperty(example = "주간 반복")
    private boolean repeatWeek;

    @ApiModelProperty(example = "메모")
    private String memo;

    @ApiModelProperty(example = "파티장 캐릭터 id / 본인 캐릭 중")
    @NotNull
    private long leaderCharacterId;

    @ApiModelProperty(example = "깐부 캐릭터 id 리스트")
    private List<Long> friendCharacterIdList;
}
