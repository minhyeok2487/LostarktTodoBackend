package lostark.todo.controller.dto.marketDto;

import lombok.Data;

@Data
public class MarketReturnDto {

    String itemName;
    private int recentPrice;
    private int bundleCount;
}
