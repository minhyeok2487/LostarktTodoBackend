package lostark.todo.domain.market;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    int countByCategoryCode(int categoryCode);

    List<Market> findByNameIn(List<String> names);

    List<Market> findByCategoryCode(int categoryCode);

}
