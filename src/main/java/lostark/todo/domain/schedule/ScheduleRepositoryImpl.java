package lostark.todo.domain.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.schedule.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domain.schedule.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<WeekScheduleResponse> getWeek(String username, GetWeekScheduleRequest request) {
        return factory
                .select(new QWeekScheduleResponse(
                        schedule.id,
                        schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.dayOfWeek, schedule.time, character.characterName
                ))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .where(
                        eqUsername(username),
                        isCurrentWeekSchedule(request)
                )
                .fetch();
    }

    private BooleanExpression isCurrentWeekSchedule(GetWeekScheduleRequest request) {
        // 반복이 없는 건 날짜가 포함된 주 것만, 반복이 있으면 다
        return schedule.repeatWeek.eq(true).or(
                schedule.repeatWeek.eq(false).and(
                        schedule.createdDate.between(
                                request.getDate().with(DayOfWeek.MONDAY).atStartOfDay(),
                                request.getDate().with(DayOfWeek.SUNDAY).plusDays(1).atStartOfDay()
                        )
                )
        );
    }

    @Override
    public Optional<GetScheduleResponse> getResponse(long scheduleId, String username) {
        GetScheduleResponse response = factory.select(new QGetScheduleResponse(
                        schedule.id, schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.time, schedule.memo, schedule.repeatWeek,
                        new QScheduleCharacterResponse(
                                character.id, character.characterName, character.characterClassName,
                                character.itemLevel, character.characterImage
                        )
                ))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .where(
                        eqId(scheduleId),
                        eqUsername(username)
                ).fetchOne();

        return Optional.ofNullable(response);
    }

    @Override
    public Optional<Schedule> get(long scheduleId, String username) {
        Schedule response = factory.selectFrom(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .where(
                        eqId(scheduleId), eqUsername(username)
                ).fetchOne();
        return Optional.ofNullable(response);
    }

    @Override
    public List<Schedule> searchFriend(long scheduleId) {
        return factory.selectFrom(schedule)
                .where(
                        eqLeaderScheduleId(scheduleId)
                ).fetch();
    }

    @Override
    public void remove(long scheduleId) {
        factory.delete(schedule)
                .where(
                        eqId(scheduleId).or(eqLeaderScheduleId(scheduleId))
                ).execute();
    }

    @Override
    public Boolean existsByCharacterAndTime(long characterId, LocalTime time, DayOfWeek dayOfWeek) {
        return factory.selectFrom(schedule)
                .where(
                        eqCharacterId(characterId),
                        eqTime(time),
                        eqDayOfWeek(dayOfWeek)
                )
                .fetchCount() > 0;
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

    private BooleanExpression eqCharacterId(long characterId) {
        return schedule.characterId.eq(characterId);
    }

    private BooleanExpression eqTime(LocalTime time) {
        return schedule.time.eq(time);
    }

    private BooleanExpression eqDayOfWeek(DayOfWeek dayOfWeek) {
        return schedule.dayOfWeek.eq(dayOfWeek);
    }

    private BooleanExpression eqId(long scheduleId) {
        return schedule.id.eq(scheduleId);
    }

    private BooleanExpression eqLeaderScheduleId(long leaderScheduleId) {
        return schedule.leaderScheduleId.eq(leaderScheduleId);
    }

    private BooleanExpression eqUsername(String username) {
        return member.username.eq(username);
    }

}
