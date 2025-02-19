package lostark.todo.domain.logs.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;

import java.time.LocalDate;

@Data
public class LogsSearchResponse {

    @ApiModelProperty(name = "로그 ID")
    private Long logsId;

    private LocalDate localDate;

    private LogType logType;

    @ApiModelProperty(name = "로그 카테고리")
    private LogContent logContent;

    private String name;

    private String message;

    private double profit;

    @QueryProjection
    public LogsSearchResponse(Long logsId, LocalDate localDate, LogType logType, LogContent logContent, String name, String message, double profit) {
        this.logsId = logsId;
        this.localDate = localDate;
        this.logType = logType;
        this.logContent = logContent;
        this.name = name;
        this.message = message;
        this.profit = profit;
    }
}
