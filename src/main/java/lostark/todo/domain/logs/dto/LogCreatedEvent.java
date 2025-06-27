package lostark.todo.domain.logs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lostark.todo.domain.logs.entity.Logs;

@Getter
@AllArgsConstructor
public class LogCreatedEvent {

    private Logs logs;
}
