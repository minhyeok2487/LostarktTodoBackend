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

    private int gate;

    private String weekCategory;
    private WeekContentCategory weekContentCategory;

    public TodoResponseDto toDto(TodoV2 todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .check(todo.isChecked())
                .name(todo.getWeekContent().getName() + " " + todo.getWeekContent().getGate())
                .gold(todo.getGold())
                .message(todo.getMessage())
                .gate(todo.getWeekContent().getGate())
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
