package lostark.todo.domainV2.util.market.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lostark.todo.Constant.LEVEL_UP_RESOURCES;

@RequiredArgsConstructor
@Repository
public class MarketDao {

    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public List<Market> findByNameIn(List<String> names) {
        return marketRepository.findByNameIn(names);
    }

    @Transactional(readOnly = true)
    public Map<String, Market> findContentResource() {
        List<Market> marketByNames = findByNameIn(LEVEL_UP_RESOURCES);
        return marketByNames.stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }
}
