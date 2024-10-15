package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friends.Friends;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;

public class CheckRaidPermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isCheckRaid()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }
    }
}
