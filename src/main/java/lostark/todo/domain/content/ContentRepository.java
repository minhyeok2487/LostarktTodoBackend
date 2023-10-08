package lostark.todo.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findContentByName(String name);

    @Query("SELECT w FROM WeekContent w")
    List<WeekContent> findAllByWeekContent();

    @Query("SELECT w FROM WeekContent w WHERE w.level <= :itemLevel AND w.id BETWEEN 17 AND 70")
    List<WeekContent> findAllByWeekContentWithItemLevelV2(@Param("itemLevel") double itemLevel);

    @Query("SELECT w FROM WeekContent w WHERE w.level <= :itemLevel AND w.id >= 71")
    List<WeekContent> findAllByWeekContentWithItemLevel(@Param("itemLevel") double itemLevel);

    List<DayContent> findDayContentByCategoryOrderByLevelDesc(@Param("category") Category category);

    @Query("SELECT w FROM WeekContent w " +
            "WHERE w.level <= :itemLevel " +
            "AND w.weekCategory = :weekCategory " +
            "AND w.weekContentCategory = :weekContentCategory")
    List<WeekContent> findAllWeekContent(@Param("itemLevel") double itemLevel,
                                         @Param("weekCategory") String weekCategory,
                                         @Param("weekContentCategory") WeekContentCategory weekContentCategory);

    @Query("SELECT w FROM WeekContent w WHERE w.weekCategory = :weekCategory AND w.gate = :gate")
    Optional<WeekContent> findByWeekCategoryAndGate(@Param("weekCategory") String weekCategory,
                                                    @Param("gate") int gate);
}
