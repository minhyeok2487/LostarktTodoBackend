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
    private int gemLevel;
    private String description;
    private String option;
    private String icon;

    public static GemHistoryResponse from(GemHistory entity) {
        return GemHistoryResponse.builder()
                .skillName(entity.getSkillName())
                .gemLevel(entity.getGemLevel())
                .description(entity.getDescription())
                .option(entity.getOption())
                .icon(entity.getIcon())
                .build();
    }
}
