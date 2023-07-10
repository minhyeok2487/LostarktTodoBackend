package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;

    public List<Market> getMarketByCategory(int categoryCode) {
        return marketRepository.findByCategoryCodeOrderByCurrentMinPriceDesc(categoryCode);
    }

    public List<Market> getMarketByNames(List<String> names) {
        return marketRepository.findByNameIn(names);
    }

    public Map<String, MarketContentResourceDto> getContentResource(List<String> names) {
        List<Market> marketByNames = marketRepository.findByNameIn(names);
        Map<String , MarketContentResourceDto> contentResourceDtoHashMap = new HashMap<>();
        for (Market marketByName : marketByNames) {
            String name = marketByName.getName();
            int recentPrice = marketByName.getRecentPrice();
            int bundleCount = marketByName.getBundleCount();
            contentResourceDtoHashMap.put(name, new MarketContentResourceDto(recentPrice, bundleCount));
        }
        return contentResourceDtoHashMap;
    }

    public Market getMarketByName(String name) {
        return marketRepository.findByName(name);
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
}
