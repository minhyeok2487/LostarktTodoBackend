package lostark.todo.domain.logs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.event.entity.character.DayContentCheckEvent;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class LogsDayContent extends Logs {

    private long characterId;

    @Enumerated(EnumType.STRING)
    private Category category; // 카오스던전, 가디언토벌, 일일에포나

    private int checkBefore;

    private int checkAfter;

    private int gaugeBefore;

    private int gaugeAfter;

    private double profit;

    public static LogsDayContent toEntity(DayContentCheckEvent characterEvent, Character character, double profit) {
        LogsDayContent logsDayContent = new LogsDayContent();
        logsDayContent.setMessage(characterEvent.getEventType().getMessage());
        logsDayContent.setCharacterId(character.getId());
        if (characterEvent.getCategory().equals("epona")) {
            logsDayContent.setCategory(Category.일일에포나);
            logsDayContent.setCheckAfter(character.getDayTodo().getEponaCheck2());
            logsDayContent.setGaugeAfter(character.getDayTodo().getEponaGauge());
        }
        if (characterEvent.getCategory().equals("chaos")) {
            logsDayContent.setCategory(Category.카오스던전);
            logsDayContent.setCheckAfter(character.getDayTodo().getChaosCheck());
            logsDayContent.setGaugeAfter(character.getDayTodo().getChaosGauge());
        }

        if (characterEvent.getCategory().equals("guardian")) {
            logsDayContent.setCategory(Category.가디언토벌);
            logsDayContent.setCheckAfter(character.getDayTodo().getGuardianCheck());
            logsDayContent.setGaugeAfter(character.getDayTodo().getGuardianGauge());
        }
        logsDayContent.setCheckBefore(characterEvent.getBeforeCheck());
        logsDayContent.setGaugeBefore(characterEvent.getBeforeGauge());
        logsDayContent.setProfit(profit);
        return logsDayContent;
    }
}
