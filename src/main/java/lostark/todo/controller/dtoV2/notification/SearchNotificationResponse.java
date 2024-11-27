package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.enums.NotificationType;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class SearchNotificationResponse {

    @NotEmpty
    @ApiModelProperty(example = "알림 id")
    private long id;

    @NotEmpty
    @ApiModelProperty(example = "생성 일자")
    private LocalDateTime createdDate;

    @NotEmpty
    @ApiModelProperty(example = "내용")
    private String content;

    @NotEmpty
    @ApiModelProperty(example = "타입")
    private NotificationType notificationType;

    @ApiModelProperty(example = "읽음 여부")
    private boolean isRead;

    @ApiModelProperty(example = "데이터")
    private JSONObject data;

    public SearchNotificationResponse(Notification notification, JSONObject data) {
        this.id = notification.getId();
        this.createdDate = notification.getCreatedDate();
        this.content = notification.getContent();
        this.notificationType = notification.getNotificationType();
        this.isRead = notification.isRead();
        this.data = data;
    }
}
