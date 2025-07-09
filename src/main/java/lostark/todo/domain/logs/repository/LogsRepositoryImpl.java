package lostark.todo.domain.logs.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.dto.*;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.domain.logs.entity.QLogs.logs;

@RequiredArgsConstructor
public class LogsRepositoryImpl implements LogsCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<Logs> get(long memberId, long characterId, LogContent logContent, LocalDate localDate, String name) {
        return factory.selectFrom(logs)
                        .where(
                                eqMember(memberId),
                                eqCharacter(characterId),
                                eqLogContent(logContent),
                                betweenThisWeek(logContent, localDate),
                                eqName(name)
                        )
                        .fetch();
    }

    @Override
    public List<Logs> getAll(long memberId, LogType logType, LocalDate localDate) {
        return factory.selectFrom(logs)
                .where(
                        eqMember(memberId),
                        eqLogType(logType),
                        eqLocalDate(localDate)
                )
                .fetch();
    }

    @Override
    public CursorResponse<LogsSearchResponse> search(long member, LogsSearchParams params, PageRequest pageRequest) {
        List<LogsSearchResponse> fetch = factory.select(new QLogsSearchResponse(
                        logs.id,
                        logs.lastModifiedDate,
                        logs.localDate,
                        logs.logType,
                        logs.logContent,
                        logs.name,
                        logs.message,
                        logs.profit,
                        character.characterClassName,
                        character.characterName
                ))
                .from(logs)
                .leftJoin(character).on(character.id.eq(logs.characterId)).fetchJoin()
                .where(
                        eqMember(member),
                        ltLogsId(params.getLogsId()),
                        eqCharacter(params.getCharacterId()),
                        eqLogContent(params.getLogContent()),
                        isDeleted(false)
                )
                .orderBy(logs.lastModifiedDate.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();
        boolean hasNext = false;

        if (fetch.size() > pageRequest.getPageSize()) {
            fetch.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        return new CursorResponse<>(fetch, hasNext);
    }

    @Override
    public List<LogProfitResponse> getProfit(long memberId, GetLogsProfitRequest request) {
        return factory.select(new QLogProfitResponse(
                        logs.localDate,
                        Expressions.numberTemplate(Double.class,
                                "SUM(CASE WHEN {0} = 'DAILY' THEN {1} ELSE 0 END)", logs.logType, logs.profit),
                        Expressions.numberTemplate(Double.class,
                                "SUM(CASE WHEN {0} = 'WEEKLY' THEN {1} ELSE 0 END)", logs.logType, logs.profit),
                        Expressions.numberTemplate(Double.class,
                                "SUM(CASE WHEN {0} = 'ETC' THEN {1} ELSE 0 END)", logs.logType, logs.profit),
                        logs.profit.sum().as("totalProfit")
                ))
                .from(logs)
                .where(
                        eqMember(memberId),
                        betweenDate(request.getStartDate(), request.getEndDate()),
                        eqCharacter(request.getCharacterId()),
                        isDeleted(false)
                )
                .groupBy(logs.localDate)
                .fetch();
    }

    private BooleanExpression eqCharacter(Long characterId) {
        if (characterId == null || characterId == 0) {
            return null;
        }
        return logs.characterId.eq(characterId);
    }

    private BooleanExpression betweenDate(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        return logs.localDate.between(start, end);
    }

    private BooleanExpression betweenThisWeek(LogContent logContent, LocalDate localDate) {
        if (logContent.equals(LogContent.CHAOS) || logContent.equals(LogContent.GUARDIAN)) {
            return eqLocalDate(localDate);
        }

        LocalDateTime now = LocalDateTime.now();

        // 이번 주 수요일 오전 6시 계산
        LocalDateTime thisWeekStart = getThisWeekWednesday6AM(now);

        // 다음 주 수요일 오전 6시 계산 (이번 주 끝)
        LocalDateTime thisWeekEnd = thisWeekStart.plusWeeks(1);

        // LocalDate로 변환 (시간 부분 제거)
        LocalDate startDate = thisWeekStart.toLocalDate();
        LocalDate endDate = thisWeekEnd.toLocalDate().minusDays(1); // 다음 주 수요일 전날까지

        return logs.localDate.between(startDate, endDate);
    }

    private LocalDateTime getThisWeekWednesday6AM(LocalDateTime now) {
        // 이번 주 수요일 오전 6시
        LocalDateTime thisWeekWednesday6AM = now
                .with(DayOfWeek.WEDNESDAY)
                .withHour(6)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // 만약 현재 시간이 이번 주 수요일 오전 6시 이전이라면
        // 지난 주 수요일 오전 6시부터 시작하는 주간을 반환
        if (now.isBefore(thisWeekWednesday6AM)) {
            return thisWeekWednesday6AM.minusWeeks(1);
        }

        return thisWeekWednesday6AM;
    }

    private BooleanExpression eqMember(long memberId) {
        return logs.memberId.eq(memberId);
    }

    private BooleanExpression eqLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return logs.localDate.eq(localDate);
    }

    private BooleanExpression eqLogContent(LogContent logContent) {
        if (logContent == null) {
            return null;
        }
        return logs.logContent.eq(logContent);
    }

    private BooleanExpression eqLogType(LogType logType) {
        if (logType == null) {
            return null;
        }
        return logs.logType.eq(logType);
    }

    private BooleanExpression eqName(String name) {
        if (name == null) {
            return null;
        }
        return logs.name.eq(name);
    }

    private BooleanExpression ltLogsId(Long logsId) {
        if (logsId != null) {
            return logs.id.lt(logsId);
        }
        return null;
    }

    private BooleanExpression isDeleted(boolean deleted) {
        return logs.deleted.eq(deleted);
    }
}