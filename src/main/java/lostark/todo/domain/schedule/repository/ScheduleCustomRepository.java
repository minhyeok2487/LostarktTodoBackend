package lostark.todo.domain.schedule.repository;

import lostark.todo.domain.schedule.dto.GetScheduleResponse;
import lostark.todo.domain.schedule.dto.ScheduleCharacterResponse;
import lostark.todo.domain.schedule.dto.SearchScheduleRequest;
import lostark.todo.domain.schedule.dto.WeekScheduleResponse;
import lostark.todo.domain.schedule.entity.Schedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScheduleCustomRepository {

    List<WeekScheduleResponse> search(String username, SearchScheduleRequest request);

    List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId);

    /**
     * 여러 leaderScheduleId로 친구 캐릭터 이름을 일괄 조회
     * @param leaderScheduleIds 리더 스케줄 ID 목록
     * @return Map<leaderScheduleId, List<캐릭터이름>>
     */
    Map<Long, List<String>> getFriendNamesByLeaderScheduleIds(List<Long> leaderScheduleIds);

    Optional<GetScheduleResponse> getResponse(long scheduleId, String username, Long leaderScheduleId);

    Optional<Schedule> get(long scheduleId, String username);

    List<Schedule> searchFriend(long scheduleId);

    void remove(long scheduleId);

    void checkScheduleRaids();

    void deleteByIdSafe(Long id);
}
