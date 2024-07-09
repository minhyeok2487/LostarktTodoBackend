package lostark.todo.controller.dtoV2.schedule;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRaidCategory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WeekScheduleResponse {

    private long scheduleId;

    @ApiModelProperty(example = "일정 종류")
    private ScheduleCategory scheduleCategory;

    @ApiModelProperty(example = "레이드 카테고리")
    private ScheduleRaidCategory scheduleRaidCategory;

    @ApiModelProperty(example = "레이드 이름")
    private String raidName;

    @ApiModelProperty(example = "일정 요일")
    private DayOfWeek dayOfWeek;

    @ApiModelProperty(example = "일정 시간 / 10분 단위")
    private LocalTime time;

    @ApiModelProperty(example = "메모")
    private String memo;

    @ApiModelProperty(example = "리더")
    private Boolean isLeader;

    @ApiModelProperty(example = "리더 스케줄 ID")
    private long leaderScheduleId;

    @ApiModelProperty(example = "내 캐릭터 이름")
    private String characterName;

    @ApiModelProperty(example = "리더 캐릭터 이름")
    private String leaderCharacterName;

    @ApiModelProperty(example = "깐부 캐릭터 닉네임 리스트")
    private List<String> friendCharacterNames;

    @QueryProjection
    public WeekScheduleResponse(long scheduleId, ScheduleCategory scheduleCategory, ScheduleRaidCategory scheduleRaidCategory,
                                String raidName, DayOfWeek dayOfWeek, LocalTime time, String memo, Boolean isLeader, long leaderScheduleId,
                                String characterName, String leaderCharacterName) {
        this.scheduleId = scheduleId;
        this.scheduleCategory = scheduleCategory;
        this.scheduleRaidCategory = scheduleRaidCategory;
        this.raidName = raidName;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.memo = memo;
        this.isLeader = isLeader;
        this.leaderScheduleId = leaderScheduleId;
        this.characterName = characterName;
        this.leaderCharacterName = leaderCharacterName;
        this.friendCharacterNames = new ArrayList<>();
    }
}
