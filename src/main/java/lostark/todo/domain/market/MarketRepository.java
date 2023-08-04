package lostark.todo.domain.market;

import lostark.todo.domain.content.DayContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {

    int countByCategoryCode(int categoryCode);

    List<Market> findByNameIn(List<String> names);

    List<Market> findByCategoryCode(int categoryCode);

    Optional<Market> findByName(String name);
}
