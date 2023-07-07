package lostark.todo.domain.content;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByLevelGreaterThanEqual(double level);

    @Query("select c from DayContent c " +
            "where c.level <= :level " +
            "and c.dayContentCategory = :contentCategory " +
            "order by c.level desc")
    List<DayContent> findDayContentByLevel(
            @Param("level") double level, @Param("contentCategory") Category category);

    DayContent findTop1ByLevelLessThanEqualOrderByLevelDesc(double level);


}
