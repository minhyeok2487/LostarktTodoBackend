package lostark.todo.domain.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.schedule.GetWeekScheduleRequest;
import lostark.todo.controller.dtoV2.schedule.QWeekScheduleResponse;
import lostark.todo.controller.dtoV2.schedule.WeekScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.schedule.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<WeekScheduleResponse> getWeek(List<Long> characterList, GetWeekScheduleRequest request) {
        return factory
                .select(new QWeekScheduleResponse(
                        schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.time, character.characterName
                ))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .where(
                        betweenDate(request.getStartDate().atStartOfDay(), request.getEndDate().atStartOfDay()),
                        containCharacterIdList(characterList)
                )
                .fetch();
    }

    private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        return schedule.time.between(startDate, endDate);
    }

    private BooleanExpression containCharacterIdList(List<Long> characterList) {
        return schedule.characterId.in(characterList);
    }
}
