package lostark.todo.domain.generaltodo.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
public enum GeneralTodoViewMode {
    LIST,
    KANBAN;

    public static GeneralTodoViewMode fromNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(mode -> mode.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported view mode: " + value));
    }
}
