package lostark.todo.domain.logs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.logs.enums.LogContent;

@Data
public class LogsSearchParams {

    @ApiModelProperty(name = "로그 ID, 첫 글이면 X")
    private Long logsId;

    @ApiModelProperty(name = "캐릭터 ID, 없으면 X")
    private Long characterId;

    @ApiModelProperty(name = "로그 카테고리, 없으면 X")
    private LogContent logContent;
}
