package lostark.todo.controller.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketContentResourceDto {
    private int recentPrice;

    private int bundleCount;
}
