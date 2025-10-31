package lostark.todo.domain.generaltodo.enums;

public enum GeneralTodoStatusType {
    PROGRESS,
    DONE;

    public boolean isDone() {
        return this == DONE;
    }
}
