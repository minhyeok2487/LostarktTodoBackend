package lostark.todo.domain.logs.repository;

import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.dto.LogsSearchParams;
import lostark.todo.domain.logs.dto.LogsSearchResponse;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface LogsCustomRepository {

    CursorResponse<LogsSearchResponse> search(long member, LogsSearchParams params, PageRequest pageRequest);

    List<LogProfitResponse> getProfit(long memberId, GetLogsProfitRequest request);

    void deleteLogsByLogs(Logs logs);

    void deleteMoreRewardLogs(long memberId, long characterId, String weekCategory);
}