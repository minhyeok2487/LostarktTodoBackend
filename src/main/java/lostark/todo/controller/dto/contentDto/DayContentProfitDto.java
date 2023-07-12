package lostark.todo.controller.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lostark.todo.domain.content.Category;

@Data
@AllArgsConstructor
public class DayContentProfitDto {
    String characterName;
    String category;
    String contentName;
    int checked;
}
