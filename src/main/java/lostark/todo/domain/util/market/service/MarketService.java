package lostark.todo.domain.util.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.util.market.repository.MarketRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.global.Constant.LEVEL_UP_RESOURCES;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MarketService {

    private final MarketRepository marketRepository;

    /**
     * 거래소 데이터 업데이트 메소드
     */
    public void updateMarketItemList(List<Market> marketList, int categoryCode) {
        exception(marketList);
        List<Market> oldList = marketRepository.findByCategoryCode(categoryCode);
        for (Market market : marketList) {
            Optional<Market> find = oldList.stream().filter(item -> item.getLostarkMarketId() == market.getLostarkMarketId()).findFirst();
            if (find.isPresent()) {
                find.get().changeData(market);
            } else {
                marketRepository.save(market);
            }
        }
    }

    public void updateAuctionItemList(List<JSONObject> jsonObjectList) {
        Map<String, Market> jewelryMap = marketRepository.findByNameIn(List.of("3티어 1레벨 보석", "4티어 1레벨 보석"))
                .stream()
                .collect(Collectors.toMap(Market::getName, market -> market));

        jsonObjectList.forEach(jsonObject -> {
            String tierKey = jsonObject.get("Tier") + "티어 1레벨 보석";
            Market market = jewelryMap.get(tierKey);
            if (market != null) {
                market.updatePrice(jsonObject);
            }
        });
    }

    private static void exception(List<Market> marketList) {
        if (marketList.isEmpty()) {
            throw new IllegalArgumentException("marketList is Empty");
        }
    }

    // 거래소 데이터 호출
    public Map<String, Market> findContentResource() {
        return marketRepository.findByNameIn(LEVEL_UP_RESOURCES).stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }
}
