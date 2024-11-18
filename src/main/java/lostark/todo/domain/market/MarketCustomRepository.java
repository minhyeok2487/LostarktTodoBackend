package lostark.todo.domain.market;

import java.util.Map;

public interface MarketCustomRepository {

    Map<String, Market> findLevelUpResource();

}
