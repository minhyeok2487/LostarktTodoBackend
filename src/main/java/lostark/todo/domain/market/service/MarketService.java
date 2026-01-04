package lostark.todo.domain.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.repository.MarketRepository;
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
     * - Map을 활용하여 O(n) 시간복잡도로 처리
     * - partitioningBy로 업데이트/신규 대상 분류
     * - saveAll로 신규 데이터 일괄 저장
     */
    @Transactional
    public void updateMarketItemList(List<Market> newMarketList, int categoryCode) {
        exception(newMarketList);

        // 기존 데이터를 Map으로 변환하여 O(1) 조회 가능하게 함
        Map<Long, Market> existingMap = marketRepository.findAllByCategoryCode(categoryCode)
                .stream()
                .collect(Collectors.toMap(Market::getLostarkMarketId, Function.identity()));

        // 기존 존재 여부에 따라 분류
        Map<Boolean, List<Market>> partitioned = newMarketList.stream()
                .collect(Collectors.partitioningBy(m -> existingMap.containsKey(m.getLostarkMarketId())));
        List<Market> toUpdate = partitioned.get(true);
        List<Market> toInsert = partitioned.get(false);

        // 기존 데이터 업데이트
        toUpdate.forEach(m -> existingMap.get(m.getLostarkMarketId()).changeData(m));

        // 신규 데이터 일괄 저장
        marketRepository.saveAll(toInsert);

        log.info("✅ 업데이트: {}, 추가: {}", toUpdate.size(), toInsert.size());
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
