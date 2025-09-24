package lostark.todo.domain.analysis.repository;

import lostark.todo.domain.analysis.entity.AnalysisDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisDetailRepository extends JpaRepository<AnalysisDetail, Long> {
    List<AnalysisDetail> findAllByAnalysisIdIn(List<Long> analysisIds);
}