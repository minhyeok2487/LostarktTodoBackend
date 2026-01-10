package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminNotificationResponse {

    private long id;
    private String content;
    private boolean isRead;
    private NotificationType notificationType;
    private long receiverId;
    private String receiverUsername;
    private LocalDateTime createdDate;

    public static AdminNotificationResponse from(Notification notification) {
        return AdminNotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .notificationType(notification.getNotificationType())
                .receiverId(notification.getReceiver() != null ? notification.getReceiver().getId() : 0)
                .receiverUsername(notification.getReceiver() != null ? notification.getReceiver().getUsername() : null)
                .createdDate(notification.getCreatedDate())
                .build();
    }
}
