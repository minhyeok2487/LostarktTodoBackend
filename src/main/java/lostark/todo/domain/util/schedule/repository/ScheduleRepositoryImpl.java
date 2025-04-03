package lostark.todo.domain.util.schedule.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.character.entity.QCharacter;
import lostark.todo.domain.util.schedule.dto.SearchScheduleRequest;
import lostark.todo.domain.util.schedule.entity.QSchedule;
import lostark.todo.domain.util.schedule.entity.Schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.domain.util.schedule.entity.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<WeekScheduleResponse> getWeek(String username, GetWeekScheduleRequest request) {
        QSchedule ls = new QSchedule("ls");
        QCharacter lc = new QCharacter("lc");

        return factory
                .select(new QWeekScheduleResponse(
                        schedule.id,
                        schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.dayOfWeek, schedule.time, schedule.memo,
                        schedule.leader, schedule.leaderScheduleId,
                        character.characterName,
                        new CaseBuilder().when(schedule.leader.eq(true)).then(character.characterName)
                                .otherwise(lc.characterName).as("leaderCharacterName"), schedule.repeatWeek, schedule.date))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .leftJoin(ls).on(schedule.leaderScheduleId.eq(ls.id))
                .leftJoin(lc).on(ls.characterId.eq(lc.id))
                .where(
                        eqUsername(username),
                        isCurrentWeekSchedule(request)
                )
                .fetch();
    }

    @Override
    public List<WeekScheduleResponse> search(String username, SearchScheduleRequest request) {
        QSchedule ls = new QSchedule("ls");
        QCharacter lc = new QCharacter("lc");

        return factory
                .select(new QWeekScheduleResponse(
                        schedule.id,
                        schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.dayOfWeek, schedule.time, schedule.memo,
                        schedule.leader, schedule.leaderScheduleId,
                        character.characterName,
                        new CaseBuilder().when(schedule.leader.eq(true)).then(character.characterName)
                                .otherwise(lc.characterName).as("leaderCharacterName"), schedule.repeatWeek, schedule.date))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .leftJoin(ls).on(schedule.leaderScheduleId.eq(ls.id))
                .leftJoin(lc).on(ls.characterId.eq(lc.id))
                .where(
                        eqUsername(username),
                        isCurrent(request)
                )
                .fetch();
    }

    // 반복이 없는 건 해당 월에 것들만, 반복이 있으면 다
    private BooleanExpression isCurrent(SearchScheduleRequest request) {
        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());
        LocalDate startOfMonth = yearMonth.atDay(1);  // 해당 월의 첫째 날
        LocalDate endOfMonth = yearMonth.atEndOfMonth(); // 해당 월의 마지막 날

        return schedule.repeatWeek.eq(true).or(
                schedule.repeatWeek.eq(false).and(
                        schedule.date.between(startOfMonth, endOfMonth)
                )
        );
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
    public Optional<GetScheduleResponse> getResponse(long scheduleId, String username, Long leaderScheduleId) {
        GetScheduleResponse response = factory.select(new QGetScheduleResponse(
                        schedule.id, schedule.scheduleCategory, schedule.scheduleRaidCategory,
                        schedule.raidName, schedule.time, schedule.memo, schedule.dayOfWeek, schedule.repeatWeek, schedule.leader,
                        new QScheduleCharacterResponse(
                                character.id, character.characterName, character.characterClassName,
                                character.itemLevel, character.characterImage
                        ), schedule.date
                ))
                .from(schedule)
                .leftJoin(character).on(schedule.characterId.eq(character.id)).fetchJoin()
                .leftJoin(member).on(character.member.eq(member))
                .where(
                        isLeaderOrNot(username, scheduleId, leaderScheduleId)
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

    private BooleanExpression eqId(long scheduleId) {
        return schedule.id.eq(scheduleId);
    }

    private BooleanExpression isLeaderOrNot(String username, long scheduleId, Long leaderScheduleId) {
        if (leaderScheduleId == null) {
            return eqId(scheduleId).and(eqUsername(username));
        } else {
            return eqId(leaderScheduleId);
        }
    }

    private BooleanExpression eqLeaderScheduleId(long leaderScheduleId) {
        return schedule.leaderScheduleId.eq(leaderScheduleId);
    }

    private BooleanExpression eqUsername(String username) {
        return member.username.eq(username);
    }
}
