package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.notification.Notification;

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

    public SearchNotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.createdDate = notification.getCreatedDate();
        this.content = notification.getContent();
    }
}
