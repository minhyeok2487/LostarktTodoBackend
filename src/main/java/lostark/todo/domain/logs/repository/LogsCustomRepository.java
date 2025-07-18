package lostark.todo.domain.logs.repository;

import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.dto.LogsSearchParams;
import lostark.todo.domain.logs.dto.LogsSearchResponse;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

public interface LogsCustomRepository {

    List<Logs> get(long memberId, long characterId, LogContent logContent, LocalDate localDate, String name);

    List<Logs> getAll(long memberId, LogType logType, LocalDate localDate);

    CursorResponse<LogsSearchResponse> search(long member, LogsSearchParams params, PageRequest pageRequest);

    List<LogProfitResponse> getProfit(long memberId, GetLogsProfitRequest request);
}