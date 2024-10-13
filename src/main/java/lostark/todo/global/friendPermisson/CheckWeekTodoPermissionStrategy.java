package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friends.Friends;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;

public class CheckWeekTodoPermissionStrategy implements UpdateCharacterStrategy {
    @Override
    public void validatePermission(Friends friend) {
        if (!friend.getFriendSettings().isCheckWeekTodo()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }
    }
}
