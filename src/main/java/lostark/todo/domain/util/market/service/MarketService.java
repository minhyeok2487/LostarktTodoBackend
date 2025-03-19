package lostark.todo.domain.util.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
    @Transactional
    public void updateMarketItemList(List<Market> newMarketList, int categoryCode) {
        exception(newMarketList);
        List<Market> oldList = marketRepository.findAllByCategoryCode(categoryCode);

        int updatedCount = 0;
        int addedCount = 0;

        for (Market market : newMarketList) {
            Optional<Market> find = oldList.stream()
                    .filter(item -> item.getLostarkMarketId() == market.getLostarkMarketId())
                    .findFirst();

            if (find.isPresent()) {
                find.get().changeData(market);
                updatedCount++;
            } else {
                marketRepository.save(market);
                addedCount++;
            }
        }
        log.info("✅ 업데이트된 거래소 데이터 개수: {}", updatedCount);
        log.info("✅ 추가된 거래소 데이터 개수: {}", addedCount);
    }

    @Transactional
    public void updateAuctionItemList(List<JSONObject> jsonObjectList) {
        Map<String, Market> jewelryMap = marketRepository.findByNameIn(
                List.of("3티어 1레벨 보석", "4티어 1레벨 보석")
        ).stream().collect(Collectors.toMap(Market::getName, Function.identity()));

        AtomicInteger updatedCount = new AtomicInteger();

        jsonObjectList.forEach(jsonObject -> {
            String tierKey = jsonObject.get("Tier") + "티어 1레벨 보석";
            Market market = jewelryMap.get(tierKey);
            if (market != null) {
                market.updatePrice(jsonObject);
                updatedCount.incrementAndGet();
            }
        });

        log.info("✅ 보석 거래소 데이터 {} (총 {}개)", updatedCount.get() == 2 ? "업데이트 완료" : "업데이트 실패", updatedCount.get());
    }



    private static void exception(List<Market> marketList) {
        if (marketList.isEmpty()) {
            throw new ConditionNotMetException("marketList is Empty");
        }
    }

    // 거래소 데이터 호출
    // TODO 추후 캐시화
    public Map<String, Market> findLevelUpResource() {
        return marketRepository.findByNameIn(LEVEL_UP_RESOURCES).stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }
}
