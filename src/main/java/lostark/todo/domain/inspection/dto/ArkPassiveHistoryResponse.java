package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.ArkPassiveHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArkPassiveHistoryResponse {

    private String category;
    private String name;
    private int level;
    private String icon;
    private String description;

    public static ArkPassiveHistoryResponse from(ArkPassiveHistory entity) {
        return ArkPassiveHistoryResponse.builder()
                .category(entity.getCategory())
                .name(entity.getName())
                .level(entity.getLevel())
                .icon(entity.getIcon())
                .description(entity.getDescription())
                .build();
    }
}
