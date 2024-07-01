package lostark.todo.controller.dto.notificationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.notification.Notification;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReturnDto {

    private long id;

    private String content;

    private boolean isRead;

    private String username;

    public static NotificationReturnDto toDto(Notification notification) {
        return NotificationReturnDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .username(notification.getReceiver().getUsername())
                .build();
    }
}
