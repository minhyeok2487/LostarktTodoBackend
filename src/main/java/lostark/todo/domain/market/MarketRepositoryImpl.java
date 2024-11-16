package lostark.todo.domain.market;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static lostark.todo.Constant.LEVEL_UP_RESOURCES;
import static lostark.todo.domain.market.QMarket.market;

@RequiredArgsConstructor
public class MarketRepositoryImpl implements MarketCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Map<String, Market> findLevelUpResource() {
        return factory
                .selectFrom(market)
                .where(market.name.in(LEVEL_UP_RESOURCES))
                .transform(GroupBy.groupBy(market.name).as(market));
    }
}
