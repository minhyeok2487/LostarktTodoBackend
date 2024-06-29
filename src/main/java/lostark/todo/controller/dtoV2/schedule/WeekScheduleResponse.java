package lostark.todo.controller.dtoV2.schedule;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRaidCategory;
import java.time.LocalDateTime;

@Data
public class WeekScheduleResponse {

    @ApiModelProperty(example = "일정 종류")
    private ScheduleCategory scheduleCategory;

    @ApiModelProperty(example = "레이드 카테고리")
    private ScheduleRaidCategory scheduleRaidCategory;

    @ApiModelProperty(example = "레이드 이름")
    private String raidName;

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    private LocalDateTime time;

    @ApiModelProperty(example = "캐릭터 이름")
    private String characterName;

    @QueryProjection
    public WeekScheduleResponse(ScheduleCategory scheduleCategory, ScheduleRaidCategory scheduleRaidCategory, String raidName, LocalDateTime time, String characterName) {
        this.scheduleCategory = scheduleCategory;
        this.scheduleRaidCategory = scheduleRaidCategory;
        this.raidName = raidName;
        this.time = time;
        this.characterName = characterName;
    }
}
