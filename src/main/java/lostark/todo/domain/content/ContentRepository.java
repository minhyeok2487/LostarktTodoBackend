package lostark.todo.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findContentByName(String name);

    @Query("SELECT w FROM WeekContent w")
    List<WeekContent> findAllByWeekContent();

    @Query("SELECT w FROM WeekContent w WHERE w.level <= :itemLevel")
    List<WeekContent> findAllByWeekContentWithItemLevel(@Param("itemLevel") double itemLevel);

    List<DayContent> findDayContentByCategoryOrderByLevelDesc(@Param("category") Category category);
}
