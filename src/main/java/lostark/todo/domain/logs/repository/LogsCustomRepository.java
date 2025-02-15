package lostark.todo.domain.logs.repository;

import lostark.todo.domain.logs.entity.Logs;

public interface LogsCustomRepository {

    void deleteLogsByLogs(Logs logs);

    void deleteMoreRewardLogs(long memberId, long characterId, String weekCategory);
}