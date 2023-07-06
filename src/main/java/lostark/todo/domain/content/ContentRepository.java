package lostark.todo.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByLevelGreaterThanEqual(double level);

    List<Content> findByLevelLessThanEqualOrderByLevelDesc(double level);



}
