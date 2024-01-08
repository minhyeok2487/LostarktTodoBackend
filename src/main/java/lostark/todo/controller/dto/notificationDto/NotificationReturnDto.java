package lostark.todo.controller.dto.notificationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.domain.notification.NotificationType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReturnDto {

    private long id;

    private String content;

    private String relatedUrl;

    private boolean isRead;

    private String username;

    public static NotificationReturnDto toDto(Notification notification) {
        return NotificationReturnDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .relatedUrl(notification.getRelatedUrl())
                .isRead(notification.isRead())
                .username(notification.getReceiver().getUsername())
                .build();
    }
}
