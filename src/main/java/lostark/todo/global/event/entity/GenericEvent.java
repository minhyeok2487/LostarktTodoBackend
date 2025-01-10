package lostark.todo.global.event.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.entity.Member;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class GenericEvent extends ApplicationEvent {

    private String title;

    private String message;

    private String username;

    public GenericEvent(Object source, String title, String message, String username) {
        super(source);
        this.username = username;
        this.title = title;
        this.message = message;
    }
}
