package lostark.todo.domain.content;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    /**
     * 레벨보다 작은 일일 컨텐츠 불러오기
     * @param category (카오스던전, 가디언토벌)
     */
    @Query("select c from DayContent c " +
            "where c.level <= :level " +
            "and c.category = :category " +
            "order by c.level desc")
    List<DayContent> findDayContentByLevel(@Param("level") double level, @Param("category") Category category);

    @Query("select c from DayContent c")
    List<DayContent> findAllDayContent();
}
