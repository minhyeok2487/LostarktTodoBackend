package lostark.todo.controller.v1.dto.marketDto;

import lombok.Data;
import lostark.todo.domain.market.Market;

@Data
public class MarketReturnDtoV1 {

    String itemName;
    private int recentPrice;
    private int bundleCount;

    public MarketReturnDtoV1(Market saved) {
        this.itemName = saved.getName();
        this.recentPrice = saved.getRecentPrice();
        this.bundleCount = saved.getBundleCount();
    }

}
