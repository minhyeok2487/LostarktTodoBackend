package lostark.todo.controller.dto.todoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponseDto {

    private long id;

    private String contentName;

    private String name;

    private int gold;

    private boolean check;

    private int sort;
}
