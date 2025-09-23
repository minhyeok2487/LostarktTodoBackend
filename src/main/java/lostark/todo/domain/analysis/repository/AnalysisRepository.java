package lostark.todo.domain.analysis.repository;

import lostark.todo.domain.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

}
