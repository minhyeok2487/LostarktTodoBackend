package lostark.todo.domain.generaltodo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Data
public class GeneralTodoItemResponse {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Long id;
    private String title;
    private String description;
    private Long folderId;
    private Long categoryId;
    private String username;
    private String dueDate;
    private boolean completed;
    private String createdAt;
    private String updatedAt;
    private Long statusId;

    @QueryProjection
    public GeneralTodoItemResponse(Long id, String title, String description, Long folderId, Long categoryId,
                                   String username, LocalDateTime dueDate, boolean completed,
                                   LocalDateTime createdAt, LocalDateTime updatedAt, Long statusId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.folderId = folderId;
        this.categoryId = categoryId;
        this.username = username;
        this.dueDate = toText(dueDate);
        this.completed = completed;
        this.createdAt = toText(createdAt);
        this.updatedAt = toText(updatedAt);
        this.statusId = statusId;
    }

    public static GeneralTodoItemResponse fromEntity(GeneralTodoItem item, String username) {
        return new GeneralTodoItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getFolder().getId(),
                item.getCategory().getId(),
                username,
                item.getDueDate(),
                item.isCompleted(),
                item.getCreatedDate(),
                item.getLastModifiedDate(),
                item.getStatus() != null ? item.getStatus().getId() : null
        );
    }

    private static String toText(LocalDateTime value) {
        return Optional.ofNullable(value)
                .map(FORMATTER::format)
                .orElse(null);
    }
}
