package lostark.todo.domain.logs.repository;

import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.entity.Logs;

import java.util.List;

public interface LogsCustomRepository {

    List<LogProfitResponse> getProfit(long memberId, GetLogsProfitRequest request);

    void deleteLogsByLogs(Logs logs);

    void deleteMoreRewardLogs(long memberId, long characterId, String weekCategory);

}