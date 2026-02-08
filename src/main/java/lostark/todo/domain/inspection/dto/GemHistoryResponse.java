package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.GemHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GemHistoryResponse {

    private String skillName;
    private int gemSlot;
    private String skillIcon;
    private int level;
    private String grade;
    private String description;
    private String gemOption;

    public static GemHistoryResponse from(GemHistory entity) {
        return GemHistoryResponse.builder()
                .skillName(entity.getSkillName())
                .gemSlot(entity.getGemSlot())
                .skillIcon(entity.getSkillIcon())
                .level(entity.getLevel())
                .grade(entity.getGrade())
                .description(entity.getDescription())
                .gemOption(entity.getGemOption())
                .build();
    }
}
