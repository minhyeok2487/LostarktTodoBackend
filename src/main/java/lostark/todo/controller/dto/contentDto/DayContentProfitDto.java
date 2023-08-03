package lostark.todo.controller.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DayContentProfitDto {
    String characterName;
    String category;
    String contentName;
    int checked;
}
