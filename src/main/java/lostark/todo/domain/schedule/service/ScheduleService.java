package lostark.todo.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.schedule.dto.*;
import lostark.todo.domain.schedule.entity.Schedule;
import lostark.todo.domain.schedule.enums.ScheduleCategory;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private Schedule get(long scheduleId, String username) {
        return scheduleRepository.get(scheduleId, username).orElseThrow(() -> new ConditionNotMetException("없는 일정 입니다."));
    }

    @Transactional
    public void create(Character character, CreateScheduleRequest request) {
        validate(request);
        Schedule leader = scheduleRepository.save(Schedule.toEntity(request, character.getId(), 0L, true));
        if (!CollectionUtils.isEmpty(request.getFriendCharacterIdList())) {
            List<Schedule> schedules = request.getFriendCharacterIdList().stream()
                    .map(friendCharacterId -> Schedule.toEntity(request, friendCharacterId, leader.getId(), false))
                    .toList();
            scheduleRepository.saveAll(schedules);
        }
    }

    private void validate(CreateScheduleRequest request) {
        if (request.getTime().getMinute() % 10 != 0) {
            throw new ConditionNotMetException("시간대는 10분 단위여야 합니다.");
        }
        if (request.getScheduleCategory() == ScheduleCategory.PARTY && CollectionUtils.isEmpty(request.getFriendCharacterIdList())) {
            throw new ConditionNotMetException("파티 일정일 경우 깐부를 추가하셔야 합니다.");
        }
        if (request.getScheduleCategory() == ScheduleCategory.ALONE && !CollectionUtils.isEmpty(request.getFriendCharacterIdList())) {
            throw new ConditionNotMetException("내 일정일 경우 깐부가 없어야 합니다.");
        }
    }

    @Transactional(readOnly = true)
    public GetScheduleResponse getResponseIsReader(long scheduleId, String username, Long leaderScheduleId) {
        return scheduleRepository.getResponse(scheduleId, username, leaderScheduleId).orElseThrow(() -> new ConditionNotMetException("없는 일정 입니다."));
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
            throw new ConditionNotMetException("파티장만 수정이 가능합니다.");
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
            throw new ConditionNotMetException("파티장만 수정이 가능합니다.");
        }

    }

    private void removeFriendsFromSchedule(List<Schedule> scheduleList, List<Long> removeFriendCharacterIdList) {
        if (removeFriendCharacterIdList != null && !removeFriendCharacterIdList.isEmpty()) {
            scheduleList.stream()
                    .filter(schedule -> removeFriendCharacterIdList.contains(schedule.getCharacterId()))
                    .forEach(scheduleRepository::delete);
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

    @Transactional
    public List<WeekScheduleResponse> search(String username, SearchScheduleRequest request) {
        return scheduleRepository.search(username, request).stream()
                .peek(response -> {
                    if (response.getScheduleCategory().equals(ScheduleCategory.PARTY)) {
                        if (response.getIsLeader()) {
                            response.setFriendCharacterNames(
                                    scheduleRepository.getLeaderScheduleId(response.getScheduleId())
                                            .stream()
                                            .map(ScheduleCharacterResponse::getCharacterName)
                                            .toList());
                        } else {
                            response.setFriendCharacterNames(
                                    scheduleRepository.getLeaderScheduleId(response.getLeaderScheduleId())
                                            .stream()
                                            .map(ScheduleCharacterResponse::getCharacterName)
                                            .toList());
                        }

                    }
                })
                .toList();
    }
}