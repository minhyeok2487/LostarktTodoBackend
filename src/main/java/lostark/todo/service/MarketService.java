package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Service;

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
}
