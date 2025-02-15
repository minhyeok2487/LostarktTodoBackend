package lostark.todo.domain.logs.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.logs.entity.Logs;
import static lostark.todo.domain.logs.entity.QLogs.logs;

@RequiredArgsConstructor
public class LogsRepositoryImpl implements LogsCustomRepository {

    private final JPAQueryFactory factory;

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