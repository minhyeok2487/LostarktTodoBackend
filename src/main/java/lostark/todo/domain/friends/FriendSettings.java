package lostark.todo.domain.friends;

import lombok.*;

import javax.persistence.Embeddable;
import java.lang.reflect.Field;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@Builder
public class FriendSettings {

    private boolean showDayTodo;

    private boolean showRaid;

    private boolean showWeekTodo;

    private boolean checkDayTodo;

    private boolean checkRaid;

    private boolean checkWeekTodo;

    private boolean updateGauge;

    private boolean updateRaid;

    private boolean setting;

    public FriendSettings() {
        this.showDayTodo = true;
        this.showRaid = true;
        this.showWeekTodo = true;
        this.checkDayTodo = false;
        this.checkRaid = false;
        this.checkWeekTodo = false;
        this.updateGauge = false;
        this.updateRaid = false;
        this.setting = false;
    }

    public void update(String name, boolean value) {
        try {
            Field field = getClass().getDeclaredField(name);
            field.setAccessible(true); // 필드에 접근할 수 있도록 설정
            field.set(this, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("없는 필드 값 입니다.");
        }
    }
}
