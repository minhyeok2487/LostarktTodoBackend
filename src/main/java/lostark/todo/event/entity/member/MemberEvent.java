package lostark.todo.event.entity.member;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class MemberEvent extends ApplicationEvent {

    private MemberEventType memberEventType;

    private Member member;

    public MemberEvent(Object source, Member member, MemberEventType memberEventType) {
        super(source);
        this.member = member;
        this.memberEventType = memberEventType;
    }
}
