package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;

public class UpdateRaidPermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isUpdateRaid()) {
            throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
        }
    }
}
