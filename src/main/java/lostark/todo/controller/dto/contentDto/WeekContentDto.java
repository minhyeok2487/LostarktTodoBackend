package lostark.todo.controller.dto.contentDto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.WeekContent;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeekContentDto {
    private long id;

    private String weekCategory;

    private String name;

    private double level;

    private int gate; //관문

    private int gold; //골드

    private boolean checked; //선택
}
