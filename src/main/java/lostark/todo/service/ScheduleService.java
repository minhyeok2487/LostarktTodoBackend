package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.schedule.Schedule;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.domain.schedule.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private Schedule get(long scheduleId, String username) {
        return scheduleRepository.get(scheduleId, username).orElseThrow(() -> new IllegalArgumentException("없는 일정 입니다."));
    }

    @Transactional
    public void create(Member member, CreateScheduleRequest request) {
        Character leaderCharacter = member.getCharacters().stream()
                .filter(character -> character.getId() == request.getLeaderCharacterId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록된 캐릭터가 아닙니다."));

        validateTime(request.getTime());

        List<Schedule> schedules = new ArrayList<>();
        Schedule leader = scheduleRepository.save(Schedule.toEntity(request, leaderCharacter.getId(), 0L, true));

        if (request.getScheduleCategory() == ScheduleCategory.PARTY) {
            request.getFriendCharacterIdList().forEach(friendCharacterId ->
                    schedules.add(Schedule.toEntity(request, friendCharacterId, leader.getId(), false))
            );
        }

        scheduleRepository.saveAll(schedules);
    }

    private void validateTime(LocalDateTime time) {
        if (time.getMinute() % 10 != 0) {
            throw new IllegalArgumentException("시간대는 10분 단위여야 합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<WeekScheduleResponse> getWeek(String username, GetWeekScheduleRequest request) {
        return scheduleRepository.getWeek(username, request);
    }

    @Transactional(readOnly = true)
    public GetScheduleResponse getResponse(long scheduleId, String username) {
        return scheduleRepository.getResponse(scheduleId, username).orElseThrow(() -> new IllegalArgumentException("없는 일정 입니다."));
    }

    @Transactional(readOnly = true)
    public List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId) {
        return scheduleRepository.getLeaderScheduleId(leaderScheduleId);
    }

    @Transactional
    public void edit(String username, EditScheduleRequest request, long scheduleId) {
        Schedule schedule = get(scheduleId, username);
        if (schedule.isLeader()) {
            schedule.edit(request);
            if (schedule.getScheduleCategory() == ScheduleCategory.PARTY) {
                List<Schedule> friendSchedules = scheduleRepository.searchFriend(scheduleId);
                for (Schedule friendSchedule : friendSchedules) {
                    friendSchedule.edit(request);
                }
            }
        } else {
            throw new IllegalStateException("파티장만 수정이 가능합니다.");
        }
    }

    @Transactional
    public void remove(String username, long scheduleId) {
        Schedule schedule = get(scheduleId, username);
        if (schedule.isLeader()) {
            scheduleRepository.remove(scheduleId);
        } else {
            scheduleRepository.delete(schedule);
        }
    }

    @Transactional
    public void editFriend(String username, EditScheduleFriendRequest request, long scheduleId) {
        Schedule main = get(scheduleId, username);
        if (main.isLeader()) {
            List<Schedule> scheduleList = scheduleRepository.searchFriend(scheduleId);
            removeFriendsFromSchedule(scheduleList, request.getRemoveFriendCharacterIdList());
            addFriendsToSchedule(main, request.getAddFriendCharacterIdList());
        } else {
            throw new IllegalStateException("파티장만 수정이 가능합니다.");
        }

    }

    private void removeFriendsFromSchedule(List<Schedule> scheduleList, List<Long> removeFriendCharacterIdList) {
        if (removeFriendCharacterIdList != null && !removeFriendCharacterIdList.isEmpty()) {
            scheduleList.removeIf(schedule -> removeFriendCharacterIdList.contains(schedule.getCharacterId()));
        }
    }

    private void addFriendsToSchedule(Schedule main, List<Long> addFriendCharacterIdList) {
        if (addFriendCharacterIdList != null && !addFriendCharacterIdList.isEmpty()) {
            addFriendCharacterIdList.forEach(id -> {
                Schedule schedule = Schedule.toEntityOfMain(main, id);
                scheduleRepository.save(schedule);
            });
        }
    }

}