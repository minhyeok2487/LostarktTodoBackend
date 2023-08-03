package lostark.todo.controller.v1.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DayContentProfitDtoV1 {
    String characterName;
    String category;
    String contentName;
    int checked;
}
