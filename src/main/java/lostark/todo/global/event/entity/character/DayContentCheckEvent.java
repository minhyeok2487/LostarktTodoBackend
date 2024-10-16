package lostark.todo.global.event.entity.character;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.global.event.entity.EventType;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class DayContentCheckEvent extends ApplicationEvent {

    private EventType eventType;

    private Character character;

    private String category;

    private int beforeCheck;

    private int beforeGauge;

    @Builder
    public DayContentCheckEvent(Object source, EventType eventType, Character character, String category, int beforeCheck, int beforeGauge) {
        super(source);
        this.eventType = eventType;
        this.character = character;
        this.category = category;
        this.beforeCheck = beforeCheck;
        this.beforeGauge = beforeGauge;
    }
}
