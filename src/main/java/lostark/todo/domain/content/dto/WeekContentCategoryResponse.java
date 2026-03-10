package lostark.todo.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lostark.todo.domain.content.enums.WeekContentCategory;

@Data
@AllArgsConstructor
public class WeekContentCategoryResponse {
    private String name;
    private String displayName;
    private int sortOrder;
    private String color;

    public static WeekContentCategoryResponse from(WeekContentCategory category) {
        return new WeekContentCategoryResponse(
                category.name(),
                category.getDisplayName(),
                category.getSortOrder(),
                category.getColor()
        );
    }
}
