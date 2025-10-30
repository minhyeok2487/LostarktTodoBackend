package lostark.todo.domain.generaltodo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.enums.GeneralTodoViewMode;

@Data
public class GeneralTodoCategoryResponse {

    private Long id;
    private String name;
    private String color;
    private Long folderId;
    private String username;
    private int sortOrder;
    private GeneralTodoViewMode viewMode;

    @QueryProjection
    public GeneralTodoCategoryResponse(Long id, String name, String color, Long folderId, String username, int sortOrder, GeneralTodoViewMode viewMode) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.folderId = folderId;
        this.username = username;
        this.sortOrder = sortOrder;
        this.viewMode = viewMode;
    }

    public static GeneralTodoCategoryResponse fromEntity(GeneralTodoCategory category, String username) {
        return new GeneralTodoCategoryResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getFolder().getId(),
                username,
                category.getSortOrder(),
                category.getViewMode()
        );
    }
}
