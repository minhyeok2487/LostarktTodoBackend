package lostark.todo.domain.logs.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.dto.*;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lostark.todo.domain.logs.entity.QLogs.logs;

@RequiredArgsConstructor
public class LogsRepositoryImpl implements LogsCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public CursorResponse<LogsSearchResponse> search(long member, LogsSearchParams params, PageRequest pageRequest) {
        List<LogsSearchResponse> fetch = factory.select(new QLogsSearchResponse(
                        logs.id,
                        logs.localDate,
                        logs.logType,
                        logs.logContent,
                        logs.name,
                        logs.message,
                        logs.profit
                ))
                .from(logs)
                .where(
                        eqMember(member),
                        eqCharacter(params.getCharacterId()),
                        eqLogContent(params.getLogContent())
                )
                .orderBy(logs.id.desc())
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
                        logs.profit.sum().as("totalProfit")
                ))
                .from(logs)
                .where(
                        eqMember(memberId),
                        betweenDate(request.getStartDate(), request.getEndDate()),
                        eqCharacter(request.getCharacterId())
                )
                .groupBy(logs.localDate)
                .fetch();
    }

    private BooleanExpression eqCharacter(Long characterId) {
        if (characterId == null) {
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

    private BooleanExpression eqMember(long memberId) {
        return logs.memberId.eq(memberId);
    }

    private BooleanExpression eqLogContent(LogContent logContent) {
        if (logContent == null) {
            return null;
        }
        return logs.logContent.eq(logContent);
    }


    @Override
    public void deleteLogsByLogs(Logs logEntity) {
        factory.delete(logs)
                .where(logs.memberId.eq(logEntity.getMemberId())
                        .and(logs.characterId.eq(logEntity.getCharacterId()))
                        .and(logs.localDate.eq(logEntity.getLocalDate()))
                        .and(logs.logType.eq(logEntity.getLogType()))
                        .and(logs.logContent.eq(logEntity.getLogContent()))
                        .and(logs.name.eq(logEntity.getName()))
                )
                .execute();
    }

    @Override
    public void deleteMoreRewardLogs(long memberId, long characterId, String weekCategory) {
        factory.delete(logs)
                .where(logs.memberId.eq(memberId)
                        .and(logs.characterId.eq(characterId))
                        .and(logs.name.eq(weekCategory))
                )
                .execute();
    }
}