package lostark.todo.domain.analysis.repository;

import lostark.todo.domain.analysis.dto.AnalysisSearchResponse;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

public interface AnalysisCustomRepository {
    CursorResponse<AnalysisSearchResponse> search(long memberId, PageRequest pageRequest);
}
