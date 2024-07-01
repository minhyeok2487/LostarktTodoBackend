package lostark.todo.controller.dtoV2.schedule;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRaidCategory;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetScheduleResponse {

    private long scheduleId;

    @ApiModelProperty(example = "일정 종류")
    private ScheduleCategory scheduleCategory;

    @ApiModelProperty(example = "레이드 카테고리")
    private ScheduleRaidCategory scheduleRaidCategory;

    @ApiModelProperty(example = "레이드 이름")
    private String raidName;

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    private LocalDateTime time;

    @ApiModelProperty(example = "메모")
    private String memo;

    @ApiModelProperty(example = "반복 날짜")
    private int repeatDay;

    @ApiModelProperty(example = "등록된 캐릭터")
    private ScheduleCharacterResponse character;

    @ApiModelProperty(example = "등록된 깐부 캐릭터 리스트")
    private List<ScheduleCharacterResponse> friendList;

    @QueryProjection
    public GetScheduleResponse(long scheduleId, ScheduleCategory scheduleCategory, ScheduleRaidCategory scheduleRaidCategory,
                               String raidName, LocalDateTime time, String memo, int repeatDay, ScheduleCharacterResponse character) {
        this.scheduleId = scheduleId;
        this.scheduleCategory = scheduleCategory;
        this.scheduleRaidCategory = scheduleRaidCategory;
        this.raidName = raidName;
        this.time = time;
        this.memo = memo;
        this.repeatDay = repeatDay;
        this.character = character;
    }
}