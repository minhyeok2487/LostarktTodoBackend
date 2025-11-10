package lostark.todo.domain.servertodo.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class ServerTodoCheckRequest {

    @NotBlank
    private String serverName;

    @NotNull
    private Boolean checked;
}
