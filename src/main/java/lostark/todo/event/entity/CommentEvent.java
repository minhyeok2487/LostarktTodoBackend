package lostark.todo.event.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

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
