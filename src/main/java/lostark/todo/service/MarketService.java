package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    /**
     * 거래소 데이터 호출
     */
    public List<Market> findByNameIn(List<String> names) {
        return marketRepository.findByNameIn(names);
    }

    public Map<String, Market> findContentResource() {
        List<Market> marketByNames = findByNameIn(dayContentResource());

        Map<String , Market> contentResourceDtoHashMap = new HashMap<>();
        for (Market marketByName : marketByNames) {
            contentResourceDtoHashMap.put(marketByName.getName(), marketByName);
        }
        return contentResourceDtoHashMap;
    }

    public List<String> dayContentResource() {
        List<String> dayContentResource = new ArrayList<>();
        dayContentResource.add("정제된 파괴강석");
        dayContentResource.add("정제된 수호강석");
        dayContentResource.add("찬란한 명예의 돌파석");

        dayContentResource.add("파괴강석");
        dayContentResource.add("수호강석");
        dayContentResource.add("경이로운 명예의 돌파석");

        dayContentResource.add("파괴석 결정");
        dayContentResource.add("수호석 결정");
        dayContentResource.add("위대한 명예의 돌파석");
        dayContentResource.add("1레벨");
        return dayContentResource;
    }


    public Market findByName(String name) {
        return marketRepository.findByName(name).orElseThrow(()->new IllegalArgumentException(name + "은 없는 아이템 입니다."));
    }
}
