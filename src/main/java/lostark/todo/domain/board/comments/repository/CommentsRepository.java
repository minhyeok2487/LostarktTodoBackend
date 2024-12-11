package lostark.todo.domain.board.comments.repository;

import lostark.todo.domain.board.comments.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long>, CommentsCustomRepository {

    @Query("SELECT c FROM Comments c WHERE c.parentId = 0")
    Page<Comments> findAllByParentIdIs0(Pageable pageable);

    List<Comments> findAllByParentId(long id);
}
