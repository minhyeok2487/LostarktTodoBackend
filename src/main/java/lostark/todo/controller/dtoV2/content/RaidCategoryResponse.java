package lostark.todo.controller.dtoV2.content;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.content.WeekContentCategory;

@Data
public class RaidCategoryResponse {
    private long categoryId;
    private String name;
    private WeekContentCategory weekContentCategory;
    private double level;

    @QueryProjection
    public RaidCategoryResponse(long categoryId, String name, WeekContentCategory weekContentCategory, double level) {
        this.categoryId = categoryId;
        this.name = name;
        this.weekContentCategory = weekContentCategory;
        this.level = level;
    }
}
