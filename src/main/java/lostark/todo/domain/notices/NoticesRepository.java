package lostark.todo.domain.notices;

import lostark.todo.domain.boards.Boards;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticesRepository extends JpaRepository<Notices, Long> {


    @Query("SELECT n FROM Notices n ORDER BY n.date DESC")
    Page<Notices> findAll(Pageable pageable);

    boolean existsByLinkContains(String noticeId);
}
