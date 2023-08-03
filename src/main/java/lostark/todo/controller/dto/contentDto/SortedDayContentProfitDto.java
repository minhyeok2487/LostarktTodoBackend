package lostark.todo.controller.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SortedDayContentProfitDto {
    private String characterName;

    private String category;

    private String contentName;

    private int checked;

    private double profit;
}
