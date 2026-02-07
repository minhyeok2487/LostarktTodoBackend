package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.ArkPassivePointHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArkPassivePointHistoryResponse {

    private String name;
    private int value;
    private String description;

    public static ArkPassivePointHistoryResponse from(ArkPassivePointHistory entity) {
        return ArkPassivePointHistoryResponse.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .description(entity.getDescription())
                .build();
    }
}
