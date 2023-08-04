package lostark.todo.controller.dto.marketDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketDto {

    private String name;

    private int recentPrice;

    private int bundleCount;
}
