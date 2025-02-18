package lostark.todo.domain.logs.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.dto.QLogProfitResponse;
import lostark.todo.domain.logs.entity.Logs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lostark.todo.domain.logs.entity.QLogs.logs;

@RequiredArgsConstructor
public class LogsRepositoryImpl implements LogsCustomRepository {

    private final JPAQueryFactory factory;

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