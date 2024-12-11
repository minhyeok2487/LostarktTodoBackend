package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friend.entity.Friends;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;

public class UpdateSettingsPermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }
    }
}
