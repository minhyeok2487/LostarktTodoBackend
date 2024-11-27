package lostark.todo.domain.util.schedule.enums;

import lombok.Getter;

@Getter
public enum ScheduleCategory {

    ALONE("내일정"),
    PARTY("파티일정");

    private String description;

    ScheduleCategory(String description) {
        this.description = description;
    }

}