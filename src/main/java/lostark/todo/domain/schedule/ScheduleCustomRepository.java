package lostark.todo.domain.schedule;

import lostark.todo.controller.dtoV2.schedule.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleCustomRepository {

    List<WeekScheduleResponse> getWeek(String username, GetWeekScheduleRequest request);

    List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId);

    Optional<GetScheduleResponse> getResponse(long scheduleId, String username);

    Optional<Schedule> get(long scheduleId, String username);

    List<Schedule> searchFriend(long scheduleId);

    void remove(long scheduleId);

    Boolean existsByCharacterAndTime(long characterId, LocalTime time, DayOfWeek dayOfWeek);
}
