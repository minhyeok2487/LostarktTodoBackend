package lostark.todo.domain.recruitingBoard;

public enum TimeCategoryEnum {

    MORNING("아침"),
    DAY("낮"),
    NIGHT("밤"),
    DAWN("새벽"),
    NONE("미정");

    private final String description;

    TimeCategoryEnum(String description) {
        this.description = description;
    }
}
