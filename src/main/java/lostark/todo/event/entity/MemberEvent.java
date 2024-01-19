package lostark.todo.event.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class MemberEvent extends ApplicationEvent {

    private EventType eventType;

    private Member member;

    public MemberEvent(Object source, Member member, EventType eventType) {
        super(source);
        this.member = member;
        this.eventType = eventType;
    }
}
