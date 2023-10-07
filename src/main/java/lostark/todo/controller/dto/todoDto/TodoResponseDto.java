package lostark.todo.controller.dto.todoDto;

import lombok.*;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.content.WeekContentCategory;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todoV2.TodoV2;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TodoResponseDto {

    private long id;

    private String name;

    private int gold;

    private boolean check;

    private String message;

    private int currentGate;
    private int totalGate;

    private String weekCategory;
    private WeekContentCategory weekContentCategory;

    public TodoResponseDto toDto(TodoV2 todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .check(false)
                .name(todo.getWeekContent().getName()
                        + "<br />" + todo.getWeekContent().getWeekContentCategory()
                        + " " + todo.getWeekContent().getGate())
                .gold(todo.getGold())
                .message(todo.getMessage())
                .currentGate(todo.isChecked() ? todo.getWeekContent().getGate() : 0)
                .totalGate(todo.getWeekContent().getGate())
                .weekCategory(todo.getWeekContent().getWeekCategory())
                .weekContentCategory(todo.getWeekContent().getWeekContentCategory())
                .build();
    }

    public TodoResponseDto toDto(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .check(todo.isChecked())
                .name(todo.getName())
                .gold(todo.getGold())
                .message(todo.getMessage())
                .build();
    }
}
