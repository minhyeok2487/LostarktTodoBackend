package lostark.todo.domain.logs.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LogProfitResponse {

    private LocalDate localDate;

    private int dayProfit;

    private int weekProfit;

    private int etcProfit;

    private int totalProfit;

    @QueryProjection
    public LogProfitResponse(LocalDate localDate, double dayProfit, double weekProfit, double etcProfit, double totalProfit) {
        this.localDate = localDate;
        this.dayProfit = (int) dayProfit;
        this.weekProfit = (int) weekProfit;
        this.etcProfit = (int) etcProfit;
        this.totalProfit = (int) totalProfit;
    }
}

