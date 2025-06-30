package lostark.todo.domain.logs.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LogsSearchResponse {

    @ApiModelProperty(name = "로그 ID")
    private Long logsId;

    private LocalDateTime createdDate;

    private LocalDate localDate;

    private LogType logType;

    @ApiModelProperty(name = "로그 카테고리")
    private LogContent logContent;

    private String name;

    private String message;

    private int profit;

    private String characterClassName;

    private String characterName;

    @QueryProjection
    public LogsSearchResponse(Long logsId, LocalDateTime createdDate, LocalDate localDate, LogType logType,
                              LogContent logContent, String name, String message, double profit,
                              String characterClassName, String characterName) {
        this.logsId = logsId;
        this.createdDate = createdDate;
        this.localDate = localDate;
        this.logType = logType;
        this.logContent = logContent;
        this.name = name;
        this.message = message;
        this.profit = (int) profit;
        this.characterClassName = characterClassName;
        this.characterName = characterName;
    }
}
