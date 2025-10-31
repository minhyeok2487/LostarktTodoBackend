package lostark.todo.domain.generaltodo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.enums.GeneralTodoStatusType;

@Data
public class GeneralTodoStatusResponse {

    private Long id;
    private Long categoryId;
    private String username;
    private String name;
    private int sortOrder;
    private GeneralTodoStatusType type;

    @QueryProjection
    public GeneralTodoStatusResponse(Long id, Long categoryId, String username, String name, int sortOrder, GeneralTodoStatusType type) {
        this.id = id;
        this.categoryId = categoryId;
        this.username = username;
        this.name = name;
        this.sortOrder = sortOrder;
        this.type = type;
    }

    public static GeneralTodoStatusResponse fromEntity(GeneralTodoStatus status, String username) {
        return new GeneralTodoStatusResponse(
                status.getId(),
                status.getCategory().getId(),
                username,
                status.getName(),
                status.getSortOrder(),
                status.getType()
        );
    }
}
