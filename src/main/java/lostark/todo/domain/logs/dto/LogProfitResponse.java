package lostark.todo.domain.logs.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LogProfitResponse {

    private LocalDate localDate;

    private int dayProfit;

    private int weekProfit;

    private int totalProfit;

    @QueryProjection
    public LogProfitResponse(LocalDate localDate, double dayProfit, double weekProfit, double totalProfit) {
        this.localDate = localDate;
        this.dayProfit = (int) dayProfit;
        this.weekProfit = (int) weekProfit;
        this.totalProfit = (int) totalProfit;
    }
}

