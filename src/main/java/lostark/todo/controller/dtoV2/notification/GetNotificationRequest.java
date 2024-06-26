package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.notification.NotificationType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetNotificationRequest {

    @ApiModelProperty(example = "회원 이메일")
    private String username;

    @ApiModelProperty(example = "알림 타입")
    private NotificationType notificationType;

    @ApiModelProperty(example = "공지사항 Id")
    private long boardId = 0;
}
