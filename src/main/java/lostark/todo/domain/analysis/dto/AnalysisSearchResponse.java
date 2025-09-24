package lostark.todo.domain.analysis.dto;

import lombok.Data;
import lostark.todo.domain.analysis.entity.Analysis;
import lostark.todo.domain.analysis.entity.AnalysisDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class AnalysisSearchResponse {

    private long id;

    private String characterName;

    private String characterClassName;

    private double itemLevel;

    private double combatPower;

    private String contentName;

    private LocalDate contentDate;

    private int battleTime;

    private Long damage;

    private Long dps;

    private Map<String, Long> analysisDetails;

    public AnalysisSearchResponse(Analysis analysis, List<AnalysisDetail> details) {
        this.id = analysis.getId();
        this.characterName = analysis.getCharacter().getCharacterName();
        this.characterClassName = analysis.getCharacter().getCharacterClassName();
        this.itemLevel = analysis.getItemLevel();
        this.combatPower = analysis.getCombatPower();
        this.contentName = analysis.getContentName();
        this.contentDate = analysis.getContentDate();
        this.battleTime = analysis.getBattleTime();
        this.damage = analysis.getDamage();
        this.dps = analysis.getDps();
        this.analysisDetails = details.stream()
                .collect(Collectors.toMap(AnalysisDetail::getAttrName, AnalysisDetail::getAttrValue));
    }
}
