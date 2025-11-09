package lostark.todo.domain.servertodo.dto;

import lombok.Builder;
import lombok.Value;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.entity.ServerTodoState;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class ServerTodoOverviewResponse {
    List<ServerTodoItemResponse> todos;
    List<ServerTodoStateResponse> states;

    public static ServerTodoOverviewResponse of(List<ServerTodo> todos, List<ServerTodoState> states) {
        return ServerTodoOverviewResponse.builder()
                .todos(todos.stream().map(ServerTodoItemResponse::from).collect(Collectors.toList()))
                .states(states.stream().map(ServerTodoStateResponse::from).collect(Collectors.toList()))
                .build();
    }
}
