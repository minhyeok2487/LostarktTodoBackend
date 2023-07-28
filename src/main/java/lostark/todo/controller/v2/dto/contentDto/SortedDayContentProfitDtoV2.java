package lostark.todo.controller.v2.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SortedDayContentProfitDtoV2 {
    private String characterName;

    private String category;

    private String contentName;

    private int checked;

    private double profit;
}
