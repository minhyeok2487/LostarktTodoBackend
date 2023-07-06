package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Market getMarketByName(String name) {
        return marketRepository.findByName(name);
    }
}
