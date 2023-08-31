package lostark.todo.controller.dto.todoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoEnumDto {

    private String name;

    private String displayName;

    private String category;

    private boolean exist;
}
