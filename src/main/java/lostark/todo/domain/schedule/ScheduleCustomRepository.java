package lostark.todo.domain.schedule;

import lostark.todo.controller.dtoV2.schedule.GetScheduleResponse;
import lostark.todo.controller.dtoV2.schedule.GetWeekScheduleRequest;
import lostark.todo.controller.dtoV2.schedule.ScheduleCharacterResponse;
import lostark.todo.controller.dtoV2.schedule.WeekScheduleResponse;
import lostark.todo.domain.member.Member;

import java.util.List;

public interface ScheduleCustomRepository {

    List<WeekScheduleResponse> getWeek(List<Long> characterList, GetWeekScheduleRequest request);

    GetScheduleResponse get(long scheduleId);

    List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId);

    void remove(Member member, long scheduleId);
}
