package lostark.todo.domain.servertodo.enums;

import java.time.DayOfWeek;

public enum VisibleWeekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static VisibleWeekday fromDayOfWeek(DayOfWeek dayOfWeek) {
        return VisibleWeekday.valueOf(dayOfWeek.name());
    }
}
