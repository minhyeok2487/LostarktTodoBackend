package lostark.todo.domain.schedule;

import lostark.todo.controller.dtoV2.schedule.GetWeekScheduleRequest;
import lostark.todo.controller.dtoV2.schedule.WeekScheduleResponse;

import java.util.List;

public interface ScheduleCustomRepository {

    List<WeekScheduleResponse> getWeek(List<Long> characterList, GetWeekScheduleRequest request);
}
