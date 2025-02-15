package lostark.todo.domain.logs.repository;

import lostark.todo.domain.logs.entity.Logs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<Logs, Long>, LogsCustomRepository {
    Page<Logs> findAllByMemberId(PageRequest request, long memberId);
}