package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.CreateScheduleRequest;
import lostark.todo.controller.dtoV2.schedule.GetWeekScheduleRequest;
import lostark.todo.controller.dtoV2.schedule.WeekScheduleResponse;
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


    @Transactional
    public void create(Member member, CreateScheduleRequest request) {
        Character leaderCharacter = member.getCharacters().stream()
                .filter(character -> character.getId() == request.getLeaderCharacterId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록된 캐릭터가 아닙니다."));

        validateTime(request.getTime());

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(Schedule.toEntity(request, leaderCharacter.getId(), true));

        if (request.getScheduleCategory() == ScheduleCategory.PARTY) {
            request.getFriendCharacterIdList().forEach(friendCharacterId ->
                    schedules.add(Schedule.toEntity(request, friendCharacterId, false))
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
    public List<WeekScheduleResponse> getWeek(Member member, GetWeekScheduleRequest request) {
        List<Long> characterList = member.getCharacters().stream().map(Character::getId).toList();
        return scheduleRepository.getWeek(characterList, request);
    }
}