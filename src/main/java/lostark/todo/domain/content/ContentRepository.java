package lostark.todo.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {

    Optional<Content> findContentByName(String name);

    @Query("SELECT w FROM WeekContent w")
    List<WeekContent> findAllByWeekContent();

    List<DayContent> findDayContentByCategoryOrderByLevelDesc(@Param("category") Category category);

    @Query("SELECT w FROM WeekContent w " +
            "WHERE w.level <= :itemLevel " +
            "AND w.weekCategory = :weekCategory " +
            "AND w.weekContentCategory = :weekContentCategory")
    List<WeekContent> findAllWeekContent(@Param("itemLevel") double itemLevel,
                                         @Param("weekCategory") String weekCategory,
                                         @Param("weekContentCategory") WeekContentCategory weekContentCategory);

    Optional<CubeContent> findByName(String name);

    @Query("SELECT d FROM DayContent d")
    List<DayContent> findAllByDayContent();
}
