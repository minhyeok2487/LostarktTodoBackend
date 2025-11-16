package lostark.todo.domain.generaltodo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String startDate;
    private String dueDate;
    @JsonProperty("isAllDay")
    private boolean allDay;
    private Long statusId;
    private String statusName;
    private String createdAt;
    private String updatedAt;

    @QueryProjection
    public GeneralTodoItemResponse(Long id, String title, String description, Long folderId, Long categoryId,
                                   String username, LocalDateTime startDate, LocalDateTime dueDate, boolean allDay,
                                   Long statusId, String statusName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.folderId = folderId;
        this.categoryId = categoryId;
        this.username = username;
        this.startDate = toText(startDate);
        this.dueDate = toText(dueDate);
        this.allDay = allDay;
        this.statusId = statusId;
        this.statusName = statusName;
        this.createdAt = toText(createdAt);
        this.updatedAt = toText(updatedAt);
    }

    public static GeneralTodoItemResponse fromEntity(GeneralTodoItem item, String username) {
        return new GeneralTodoItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getFolder().getId(),
                item.getCategory().getId(),
                username,
                item.getStartDate(),
                item.getDueDate(),
                item.isAllDay(),
                item.getStatus().getId(),
                item.getStatus().getName(),
                item.getCreatedDate(),
                item.getLastModifiedDate()
        );
    }

    private static String toText(LocalDateTime value) {
        return Optional.ofNullable(value)
                .map(FORMATTER::format)
                .orElse(null);
    }
}
