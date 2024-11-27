package lostark.todo.domain.util.market.repository;

import lostark.todo.domain.util.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long>, MarketCustomRepository {

    List<Market> findByNameIn(List<String> names);

    List<Market> findByCategoryCode(int categoryCode);

    Optional<Market> findByName(String name);
}
