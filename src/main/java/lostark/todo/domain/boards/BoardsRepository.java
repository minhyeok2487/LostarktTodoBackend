package lostark.todo.domain.boards;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardsRepository extends JpaRepository<Boards, Long>, BoardsCustomRepository {

    @Query("SELECT b FROM Boards b WHERE b.isNotice = false ORDER BY b.createdDate DESC")
    Page<Boards> findAllByNoticeFalse(Pageable pageable);

    @Query("SELECT b FROM Boards b ORDER BY b.createdDate DESC")
    Page<Boards> findAll(Pageable pageable);

    @Query("SELECT b FROM Boards b WHERE b.isNotice = true ORDER BY b.createdDate DESC")
    List<Boards> findAllByNoticeIsTrue();
}
