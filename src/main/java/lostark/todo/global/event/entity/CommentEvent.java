package lostark.todo.global.event.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class CommentEvent extends ApplicationEvent {

    private String message;

    private Member member;

    public CommentEvent(Object source, Member member, String message) {
        super(source);
        this.member = member;
        this.message = message;
    }
}
