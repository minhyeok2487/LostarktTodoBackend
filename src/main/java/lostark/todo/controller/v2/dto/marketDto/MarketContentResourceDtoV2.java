package lostark.todo.controller.v2.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketContentResourceDtoV2 {
    private int recentPrice;

    private int bundleCount;
}
