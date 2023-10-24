package lostark.todo.domain.character;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekTodo {

    private int weekEpona;

    private boolean silmaelChange;

    private int CubeTicket;

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
}
