package lostark.todo.domain.character.entity;

import lombok.*;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import javax.persistence.Embeddable;
import java.util.Set;

import static lostark.todo.domain.character.constants.ElysianConstants.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekTodo {

    /**
     * 수요일 오전 6시 주간 초기화 대상 필드 목록.
     * 새 필드 추가 시 여기에도 추가해야 함 (누락 시 WeekTodoResetSyncTest 실패).
     */
    public static final Set<String> WEEKLY_RESET_FIELDS = Set.of(
            "weekEpona", "silmaelChange", "elysianCount", "halHourglass"
    );

    private int weekEpona;

    private boolean silmaelChange;

    private int cubeTicket;

    private int elysianCount;

    private int hellKey;

    private boolean halHourglass;

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

    public void resetElysianCount() {
        if ( elysianCount >= MAX_ELYSIAN_COUNT) {
            this.elysianCount = MIN_ELYSIAN_COUNT;
        } else {
            this.elysianCount = MAX_ELYSIAN_COUNT;
        }
    }

    public void updateHellKey(int num) {
        this.hellKey = Math.max(0, this.hellKey + num);
    }

    public void updateHalHourglass() {
        this.halHourglass = !this.halHourglass;
    }
}
