package lostark.todo.domain.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.schedule.*;

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
                        schedule.id,
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

    @Override
    public GetScheduleResponse get(long scheduleId) {
        return factory.select(new QGetScheduleResponse(
                        schedule.id, schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.time, schedule.memo, schedule.repeatDay,
                        new QScheduleCharacterResponse(
                                character.id, character.characterName, character.characterClassName,
                                character.itemLevel, character.characterImage
                        )
                ))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .where(
                        eqId(scheduleId)
                ).fetchOne();
    }

    @Override
    public List<ScheduleCharacterResponse> getLeaderScheduleId(long leaderScheduleId) {
        return factory.select(new QScheduleCharacterResponse(
                        character.id, character.characterName, character.characterClassName,
                        character.itemLevel, character.characterImage))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .where(
                        eqLeaderScheduleId(leaderScheduleId)
                ).fetch();
    }

    private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        return schedule.time.between(startDate, endDate);
    }

    private BooleanExpression containCharacterIdList(List<Long> characterList) {
        return schedule.characterId.in(characterList);
    }

    private BooleanExpression eqId(long scheduleId) {
        return schedule.id.eq(scheduleId);
    }

    private BooleanExpression eqLeaderScheduleId(long leaderScheduleId) {
        return schedule.leaderScheduleId.eq(leaderScheduleId);
    }

}
