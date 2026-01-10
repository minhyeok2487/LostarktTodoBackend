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
        return switch (dayOfWeek) {
            case MONDAY -> MONDAY;
            case TUESDAY -> TUESDAY;
            case WEDNESDAY -> WEDNESDAY;
            case THURSDAY -> THURSDAY;
            case FRIDAY -> FRIDAY;
            case SATURDAY -> SATURDAY;
            case SUNDAY -> SUNDAY;
        };
    }
}
