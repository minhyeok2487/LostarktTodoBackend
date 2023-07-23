package lostark.todo.domain.market;

import lostark.todo.domain.content.DayContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    List<Market> findByCategoryCodeOrderByCurrentMinPriceDesc(int categoryCode);

    List<Market> findByNameIn(List<String> names);

    List<Market> findByCategoryCode(int categoryCode);

    Market findByName(String name);
}
