package lostark.todo.domain.content.repository;

import lostark.todo.domain.content.entity.Content;
import lostark.todo.domain.content.entity.CubeContent;
import lostark.todo.domain.content.entity.DayContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {

    @Query("SELECT d FROM DayContent d")
    List<DayContent> findAllByDayContent();

    @Query("SELECT c from CubeContent c")
    List<CubeContent> findAllByCubeContent();

    @Query("SELECT c FROM Content c WHERE TYPE(c) = :type")
    List<Content> findAllByType(@Param("type") Class<? extends Content> type);

}
