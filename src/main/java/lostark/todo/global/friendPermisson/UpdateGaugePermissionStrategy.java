package lostark.todo.global.friendPermisson;

import lostark.todo.domainV2.friend.entity.Friends;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;

public class UpdateGaugePermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isUpdateGauge()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }
    }
}
