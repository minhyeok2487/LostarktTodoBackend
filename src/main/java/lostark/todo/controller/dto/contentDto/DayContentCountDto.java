package lostark.todo.controller.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayContentCountDto {

    String characterName;
    Category category;
}
