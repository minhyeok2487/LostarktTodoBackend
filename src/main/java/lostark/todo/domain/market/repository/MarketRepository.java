package lostark.todo.domain.market.repository;

import lostark.todo.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long>, MarketCustomRepository {

    List<Market> findByNameIn(List<String> names);

    List<Market> findAllByCategoryCode(int categoryCode);
}
