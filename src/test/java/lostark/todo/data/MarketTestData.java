package lostark.todo.data;

import lostark.todo.domain.market.Market;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketTestData {

    public static Map<String, Market> createMockMarketMap() {
        return createMockMarkets().stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }

    public static List<Market> createMockMarkets() {
        return Arrays.asList(
                createMarket(24L, "찬란한 명예의 돌파석", 18, "희귀", 1, 50000),
                createMarket(32L, "정제된 파괴강석", 6, "일반", 10, 50000),
                createMarket(35L, "경이로운 명예의 돌파석", 1, "희귀", 1, 50000),
                createMarket(36L, "위대한 명예의 돌파석", 3, "희귀", 1, 50000),
                createMarket(40L, "파괴석 결정", 1, "일반", 10, 50000),
                createMarket(41L, "파괴강석", 1, "일반", 10, 50000),
                createMarket(43L, "정제된 수호강석", 3, "일반", 10, 50000),
                createMarket(51L, "수호석 결정", 1, "일반", 10, 50000),
                createMarket(52L, "수호강석", 1, "일반", 10, 50000),
                createMarket(69L, "운명의 돌파석", 13, "희귀", 1, 50000),
                createMarket(70L, "운명의 파괴석", 33, "일반", 10, 50000),
                createMarket(71L, "운명의 수호석", 9, "일반", 10, 50000),
                createMarket(73L, "3티어 1레벨 보석", 16, "고급", 1, 210000),
                createMarket(74L, "4티어 1레벨 보석", 151, "희귀", 1, 210000)
        );
    }

    private static Market createMarket(
            Long id,
            String name,
            int currentMinPrice,
            String grade,
            int bundleCount,
            int categoryCode
    ) {
        return Market.builder()
                .id(id)
                .name(name)
                .currentMinPrice(currentMinPrice)
                .recentPrice(currentMinPrice)
                .grade(grade)
                .bundleCount(bundleCount)
                .categoryCode(categoryCode)
                .icon("test.png")
                .build();
    }
}
