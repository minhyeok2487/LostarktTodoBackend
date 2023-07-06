package lostark.todo.domain.market;

import lostark.todo.domain.content.DayContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    List<Market> findByCategoryCodeOrderByCurrentMinPriceDesc(int categoryCode);
}
