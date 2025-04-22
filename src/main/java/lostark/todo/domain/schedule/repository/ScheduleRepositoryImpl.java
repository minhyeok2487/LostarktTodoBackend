package lostark.todo.domain.schedule.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.entity.QCharacter;
import lostark.todo.domain.character.entity.QTodoV2;
import lostark.todo.domain.schedule.dto.*;
import lostark.todo.domain.schedule.entity.QSchedule;
import lostark.todo.domain.schedule.entity.Schedule;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.domain.schedule.entity.QSchedule.schedule;

@RequiredArgsConstructor
@Slf4j
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory factory;

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
    public void checkScheduleRaids() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek today = now.getDayOfWeek();
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0);

        QTodoV2 todo = QTodoV2.todoV2;
        QSchedule schedule = QSchedule.schedule;

        // 1. 반복 일정 대상 bulk update
        long repeatUpdated = factory
                .update(todo)
                .set(todo.isChecked, true)
                .where(
                        JPAExpressions
                                .selectOne()
                                .from(schedule)
                                .where(
                                        schedule.repeatWeek.isTrue(),
                                        schedule.dayOfWeek.eq(today),
                                        schedule.time.eq(currentTime),
                                        schedule.characterId.eq(todo.character.id),
                                        schedule.raidName.eq(
                                                Expressions.stringTemplate(
                                                        "concat({0}, ' ', {1})",
                                                        todo.weekContent.name,
                                                        todo.weekContent.weekContentCategory.stringValue()
                                                )
                                        )
                                )
                                .exists()
                )
                .execute();

        log.info(now + " 반복 레이드 일정 체크, " + repeatUpdated + "개");

        // 2. 단건 일정 대상 bulk update
        long onceUpdated = factory
                .update(todo)
                .set(todo.isChecked, true)
                .where(
                        JPAExpressions
                                .selectOne()
                                .from(schedule)
                                .where(
                                        schedule.repeatWeek.isFalse(),
                                        schedule.date.eq(currentDate),
                                        schedule.time.eq(currentTime),
                                        schedule.characterId.eq(todo.character.id),
                                        schedule.raidName.eq(
                                                Expressions.stringTemplate(
                                                        "concat({0}, ' ', {1})",
                                                        todo.weekContent.name,
                                                        todo.weekContent.weekContentCategory.stringValue()
                                                )
                                        )
                                )
                                .exists()
                )
                .execute();

        log.info(now + " 단건 레이드 일정 체크, " + onceUpdated + "개");
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
