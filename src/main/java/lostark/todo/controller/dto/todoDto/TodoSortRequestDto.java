package lostark.todo.controller.dto.todoDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TodoSortRequestDto {

    private String weekCategory;

    private int sortNumber;
}
