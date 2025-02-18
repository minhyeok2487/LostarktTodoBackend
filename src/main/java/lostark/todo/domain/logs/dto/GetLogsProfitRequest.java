package lostark.todo.domain.logs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GetLogsProfitRequest {

    private Long characterId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;
}