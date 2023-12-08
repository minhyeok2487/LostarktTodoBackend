package lostark.todo.domain.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Query("SELECT c FROM Comments c WHERE c.parentId = 0")
    Page<Comments> findAllByParentIdIs0(Pageable pageable);

    List<Comments> findAllByParentId(long id);
}
