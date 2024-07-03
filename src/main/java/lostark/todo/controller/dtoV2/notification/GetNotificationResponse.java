package lostark.todo.controller.dtoV2.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.notification.Notification;
import lostark.todo.domain.notification.NotificationType;
import org.json.simple.JSONObject;

@Data
public class GetNotificationResponse {

    @ApiModelProperty(example = "타입")
    private NotificationType notificationType;

    @ApiModelProperty(example = "데이터")
    private JSONObject data;

    public GetNotificationResponse toBoard(long boardId) {
        this.notificationType = NotificationType.BOARD;
        JSONObject object = new JSONObject();
        object.put("boardId", boardId);
        this.data = object;
        return this;
    }

    public GetNotificationResponse toComment(long commentId, int page) {
        this.notificationType = NotificationType.COMMENT;
        JSONObject object = new JSONObject();
        object.put("commentId", commentId);
        object.put("page", page);
        this.data = object;
        return this;
    }

    public GetNotificationResponse toFriend(Notification notification) {
        this.notificationType = NotificationType.FRIEND;
        JSONObject object = new JSONObject();
        object.put("friendId", notification.getFriendId());
        object.put("friendUsername", notification.getFriendUsername());
        object.put("friendCharacterName", notification.getFriendCharacterName());
        this.data = object;
        return this;
    }
}
