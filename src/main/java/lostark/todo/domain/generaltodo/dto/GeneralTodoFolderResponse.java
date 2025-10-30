package lostark.todo.domain.generaltodo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;

@Data
public class GeneralTodoFolderResponse {

    private Long id;
    private String name;
    private String username;
    private int sortOrder;

    @QueryProjection
    public GeneralTodoFolderResponse(Long id, String name, String username, int sortOrder) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.sortOrder = sortOrder;
    }

    public static GeneralTodoFolderResponse fromEntity(GeneralTodoFolder folder, String username) {
        return new GeneralTodoFolderResponse(folder.getId(), folder.getName(), username, folder.getSortOrder());
    }
}
