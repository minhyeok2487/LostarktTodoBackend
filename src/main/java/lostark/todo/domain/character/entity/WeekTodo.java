package lostark.todo.domain.character.entity;

import lombok.*;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import javax.persistence.Embeddable;

import static lostark.todo.domain.character.constants.ElysianConstants.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekTodo {

    private int weekEpona;

    private boolean silmaelChange;

    private int cubeTicket;

    private int elysianCount;

    public void updateWeekEpona() {
        if(this.weekEpona <3) {
            this.weekEpona++;
        } else {
            this.weekEpona = 0;
        }
    }

    public void updateSilmael() {
        this.silmaelChange = !this.silmaelChange;
    }

    public void updateCubeTicket(int num) {
        this.cubeTicket += num;
    }

    public void incrementElysianCount() {
        if (elysianCount < MAX_ELYSIAN_COUNT) {
            this.elysianCount++;
        } else {
            throw new ConditionNotMetException(MAX_ELYSIAN_ERROR_MESSAGE);
        }
    }

    public void decrementElysianCount() {
        if (elysianCount > MIN_ELYSIAN_COUNT) {
            this.elysianCount--;
        } else {
            throw new ConditionNotMetException(MIN_ELYSIAN_ERROR_MESSAGE);
        }
    }
}
