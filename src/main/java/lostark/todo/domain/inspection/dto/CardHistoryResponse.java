package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.CardHistory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHistoryResponse {

    private int slot;
    private String name;
    private String icon;
    private int awakeCount;
    private int awakeTotal;
    private String grade;

    public static CardHistoryResponse from(CardHistory entity) {
        return CardHistoryResponse.builder()
                .slot(entity.getSlot())
                .name(entity.getName())
                .icon(entity.getIcon())
                .awakeCount(entity.getAwakeCount())
                .awakeTotal(entity.getAwakeTotal())
                .grade(entity.getGrade())
                .build();
    }
}
