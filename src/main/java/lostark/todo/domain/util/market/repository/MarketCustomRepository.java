package lostark.todo.domain.util.market.repository;

import lostark.todo.domain.util.market.entity.Market;

import java.util.Map;

public interface MarketCustomRepository {

    Map<String, Market> findLevelUpResource();

}
