package lostark.todo.domain.schedule;

public enum ScheduleRaidCategory {

    GUARDIAN("가디언토벌"),
    RAID("레이드"),
    ETC("기타");

    private String description;

    ScheduleRaidCategory(String description) {
        this.description = description;
    }
}
