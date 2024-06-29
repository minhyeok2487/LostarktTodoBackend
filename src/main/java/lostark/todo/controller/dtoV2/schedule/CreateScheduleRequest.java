package lostark.todo.controller.dtoV2.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRaidCategory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    @NotNull
    private LocalDateTime time;

    @ApiModelProperty(example = "반복 날짜 / 반복 없으면 null")
    @Max(7)
    @Min(1)
    private int repeatDay;

    @ApiModelProperty(example = "메모")
    private String memo;

    @ApiModelProperty(example = "파티장 캐릭터 id / 본인 캐릭 중")
    @NotNull
    private long leaderCharacterId;

    @ApiModelProperty(example = "깐부 캐릭터 id 리스트")
    private List<Long> friendCharacterIdList;
}
