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
        if(weekEpona<3) {
            weekEpona++;
        } else {
            weekEpona = 0;
        }
    }

    public void updateSilmael() {
        this.silmaelChange = !this.silmaelChange;
    }
}
