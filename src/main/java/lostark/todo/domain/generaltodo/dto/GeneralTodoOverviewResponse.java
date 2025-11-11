package lostark.todo.domain.generaltodo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralTodoOverviewResponse {

    private List<GeneralTodoFolderResponse> folders = Collections.emptyList();
    private List<GeneralTodoCategoryResponse> categories = Collections.emptyList();
    private List<GeneralTodoItemResponse> todos = Collections.emptyList();
    private List<GeneralTodoStatusResponse> statuses = Collections.emptyList();
}
