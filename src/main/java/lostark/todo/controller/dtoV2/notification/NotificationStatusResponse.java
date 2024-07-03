package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationStatusResponse {

    @NotEmpty
    @ApiModelProperty(example = "최근 생성 일자")
    private LocalDateTime latestCreatedDate;

    @NotEmpty
    @ApiModelProperty(example = "안읽은 알림의 갯수")
    private long unreadCount;
}
