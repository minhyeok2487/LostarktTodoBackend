package lostark.todo.domain.notices;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticesRepository extends JpaRepository<Notices, Long> {


    Notices findTopByOrderByIdDesc();

    boolean existsByLinkContains(String noticeId);
}
