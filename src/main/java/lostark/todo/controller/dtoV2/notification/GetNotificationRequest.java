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

    @ApiModelProperty(example = "공지사항 Id / 공지사항 타입이 아니면 0")
    private long boardId = 0;

    @ApiModelProperty(example = "방명록 Id / 방명록 타입이 아니면 0")
    private long commentId = 0;
}
