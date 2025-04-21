package lostark.todo.domain.schedule.repository;

import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.schedule.dto.SearchScheduleRequest;
import lostark.todo.domain.schedule.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleCustomRepository {

    List<WeekScheduleResponse> search(String username, SearchScheduleRequest request);

    List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId);

    Optional<GetScheduleResponse> getResponse(long scheduleId, String username, Long leaderScheduleId);

    Optional<Schedule> get(long scheduleId, String username);

    List<Schedule> searchFriend(long scheduleId);

    void remove(long scheduleId);

    void checkScheduleRaids();
}
