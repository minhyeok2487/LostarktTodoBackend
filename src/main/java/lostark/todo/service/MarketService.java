package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lostark.todo.Constant.LEVEL_UP_RESOURCES;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MarketService {

    private final MarketRepository marketRepository;

    public List<Market> findAll() {
        return marketRepository.findAll();
    }

    /**
     * 거래소 데이터 업데이트 메소드
     */
    public List<Market> updateMarketItemList(List<Market> marketList, int categoryCode) {
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
        return oldList;
    }

    private static void exception(List<Market> marketList) {
        if (marketList.isEmpty()) {
            throw new IllegalArgumentException("marketList is Empty");
        }
        if (marketList == null) {
            throw new NullPointerException("marketList is Null");
        }
    }

    // 거래소 데이터 호출
    public Map<String, Market> findContentResource() {
        List<Market> marketByNames = findByNameIn(LEVEL_UP_RESOURCES);
        return marketByNames.stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }

    public List<Market> findByNameIn(List<String> names) {
        return marketRepository.findByNameIn(names);
    }


    public Market findByName(String name) {
        return marketRepository.findByName(name).orElseThrow(()->new IllegalArgumentException(name + "은 없는 아이템 입니다."));
    }
}
