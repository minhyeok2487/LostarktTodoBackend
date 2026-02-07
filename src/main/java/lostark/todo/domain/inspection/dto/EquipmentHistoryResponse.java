package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.EquipmentHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentHistoryResponse {

    private String type;
    private String name;
    private String icon;
    private String grade;
    private Integer itemLevel;
    private Integer quality;
    private Integer refinement;
    private Integer advancedRefinement;
    private String basicEffect;
    private String additionalEffect;
    private String arkPassiveEffect;
    private String grindingEffect;
    private String braceletEffect;
    private String engravings;

    public static EquipmentHistoryResponse from(EquipmentHistory entity) {
        return EquipmentHistoryResponse.builder()
                .type(entity.getType())
                .name(entity.getName())
                .icon(entity.getIcon())
                .grade(entity.getGrade())
                .itemLevel(entity.getItemLevel())
                .quality(entity.getQuality())
                .refinement(entity.getRefinement())
                .advancedRefinement(entity.getAdvancedRefinement())
                .basicEffect(entity.getBasicEffect())
                .additionalEffect(entity.getAdditionalEffect())
                .arkPassiveEffect(entity.getArkPassiveEffect())
                .grindingEffect(entity.getGrindingEffect())
                .braceletEffect(entity.getBraceletEffect())
                .engravings(entity.getEngravings())
                .build();
    }
}
