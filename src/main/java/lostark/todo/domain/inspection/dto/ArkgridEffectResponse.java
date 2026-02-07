package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.ArkgridEffectHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArkgridEffectResponse {

    private String effectName;
    private int effectLevel;
    private String effectTooltip;

    public static ArkgridEffectResponse from(ArkgridEffectHistory entity) {
        return ArkgridEffectResponse.builder()
                .effectName(entity.getEffectName())
                .effectLevel(entity.getEffectLevel())
                .effectTooltip(entity.getEffectTooltip())
                .build();
    }
}
