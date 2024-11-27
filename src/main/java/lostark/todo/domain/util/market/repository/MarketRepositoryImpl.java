package lostark.todo.domain.util.market.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.util.market.entity.Market;

import java.util.Map;

import static lostark.todo.global.Constant.LEVEL_UP_RESOURCES;
import static lostark.todo.domain.util.market.entity.QMarket.market;

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
