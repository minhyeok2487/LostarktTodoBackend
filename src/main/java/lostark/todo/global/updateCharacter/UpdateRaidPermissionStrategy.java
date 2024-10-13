package lostark.todo.global.updateCharacter;

import lostark.todo.domain.friends.Friends;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;

public class UpdateRaidPermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isUpdateRaid()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }
    }
}
