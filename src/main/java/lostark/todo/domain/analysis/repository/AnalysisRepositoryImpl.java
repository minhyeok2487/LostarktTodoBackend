package lostark.todo.domain.analysis.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.analysis.dto.AnalysisSearchResponse;
import lostark.todo.domain.analysis.entity.Analysis;
import lostark.todo.domain.analysis.entity.QAnalysis;
import lostark.todo.domain.character.entity.QCharacter;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AnalysisRepositoryImpl implements AnalysisCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final AnalysisDetailRepository analysisDetailRepository;

    @Override
    public CursorResponse<AnalysisSearchResponse> search(long memberId, PageRequest pageRequest) {
        QAnalysis analysis = QAnalysis.analysis;
        QCharacter character = QCharacter.character;

        List<Analysis> analyses = queryFactory
                .selectFrom(analysis)
                .join(analysis.character, character).fetchJoin()
                .where(
                        character.member.id.eq(memberId)
                )
                .orderBy(analysis.id.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (analyses.size() > pageRequest.getPageSize()) {
            analyses.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        List<Long> analysisIds = analyses.stream().map(Analysis::getId).collect(Collectors.toList());
        Map<Long, List<lostark.todo.domain.analysis.entity.AnalysisDetail>> detailsMap = analysisDetailRepository.findAllByAnalysisIdIn(analysisIds)
                .stream().collect(Collectors.groupingBy(detail -> detail.getAnalysis().getId()));

        List<AnalysisSearchResponse> responseList = analyses.stream()
                .map(a -> new AnalysisSearchResponse(a, detailsMap.getOrDefault(a.getId(), List.of())))
                .collect(Collectors.toList());

        return new CursorResponse<>(responseList, hasNext);
    }
}
