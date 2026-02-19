package lostark.todo.domain.generaltodo.dto;

import lombok.Data;

@Data
public class SearchGeneralTodoRequest {

    private String query;
    private Long folderId;
    private Long categoryId;
    private Long statusId;
}
