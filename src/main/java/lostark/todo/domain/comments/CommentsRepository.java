package lostark.todo.domain.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long>, CommentsCustomRepository {

    @Query("SELECT c FROM Comments c WHERE c.parentId = 0")
    Page<Comments> findAllByParentIdIs0(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comments c WHERE c.id > :commentId AND c.parentId = 0")
    int findCommentIndex(@Param("commentId") long commentId);

    List<Comments> findAllByParentId(long id);
}
