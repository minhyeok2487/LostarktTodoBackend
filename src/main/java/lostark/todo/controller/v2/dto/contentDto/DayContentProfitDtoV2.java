package lostark.todo.controller.v2.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DayContentProfitDtoV2 {
    String characterName;
    String category;
    String contentName;
    int checked;
}
