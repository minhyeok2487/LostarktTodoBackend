package lostark.todo.domain.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.analysis.dto.UpdateAnalysisRequest;
import lostark.todo.domain.analysis.entity.Analysis;
import lostark.todo.domain.analysis.entity.AnalysisDetail;
import lostark.todo.domain.analysis.repository.AnalysisDetailRepository;
import lostark.todo.domain.analysis.repository.AnalysisRepository;
import lostark.todo.domain.character.entity.Character;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisDetailRepository analysisDetailRepository;

    @Transactional
    public void updateAnalysis(Character character, UpdateAnalysisRequest request) {
        Analysis analysis = saveAnalysis(character, request);

        if (request.getCustomData() != null && !request.getCustomData().isEmpty()) {
            saveAnalysisDetails(analysis, request.getCustomData());
        }
    }

    private Analysis saveAnalysis(Character character, UpdateAnalysisRequest request) {
        Analysis analysis = Analysis.builder()
                .character(character)
                .itemLevel(character.getItemLevel())
                .combatPower(character.getCombatPower())
                .contentName(request.getContentName())
                .battleTime(request.getBattleTime())
                .damage(request.getDamage())
                .dps(request.getDps())
                .build();

        return analysisRepository.save(analysis);
    }

    private void saveAnalysisDetails(Analysis analysis, Map<String, Long> customData) {
        List<AnalysisDetail> details = customData.entrySet().stream()
                .map(entry -> AnalysisDetail.builder()
                        .analysis(analysis)
                        .attrName(entry.getKey())
                        .attrValue(entry.getValue())
                        .build())
                .toList();

        analysisDetailRepository.saveAll(details);
    }
}

