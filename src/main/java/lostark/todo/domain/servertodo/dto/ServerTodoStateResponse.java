package lostark.todo.domain.servertodo.dto;

import lombok.Builder;
import lombok.Value;
import lostark.todo.domain.servertodo.entity.ServerTodoState;

@Value
@Builder
public class ServerTodoStateResponse {
    Long todoId;
    String serverName;
    boolean enabled;
    boolean checked;

    public static ServerTodoStateResponse from(ServerTodoState state) {
        return ServerTodoStateResponse.builder()
                .todoId(state.getServerTodo().getId())
                .serverName(state.getServerName())
                .enabled(state.isEnabled())
                .checked(state.isChecked())
                .build();
    }
}
