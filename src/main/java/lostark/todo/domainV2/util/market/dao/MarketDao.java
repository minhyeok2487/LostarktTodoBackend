package lostark.todo.domainV2.util.market.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MarketDao {

    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public List<Market> findByNameIn(List<String> names) {
        return marketRepository.findByNameIn(names);
    }
}
