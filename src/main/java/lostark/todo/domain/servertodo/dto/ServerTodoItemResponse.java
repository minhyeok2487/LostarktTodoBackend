package lostark.todo.domain.servertodo.dto;

import lombok.Builder;
import lombok.Value;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;

import java.util.Set;

@Value
@Builder
public class ServerTodoItemResponse {
    Long todoId;
    String contentName;
    boolean defaultEnabled;
    Set<VisibleWeekday> visibleWeekdays;

    public static ServerTodoItemResponse from(ServerTodo serverTodo) {
        return ServerTodoItemResponse.builder()
                .todoId(serverTodo.getId())
                .contentName(serverTodo.getContentName())
                .defaultEnabled(serverTodo.isDefaultEnabled())
                .visibleWeekdays(Set.copyOf(serverTodo.getVisibleWeekdays()))
                .build();
    }
}
