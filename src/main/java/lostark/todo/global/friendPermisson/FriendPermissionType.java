package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friends.Friends;

public enum FriendPermissionType {

    UPDATE_RAID(new UpdateRaidPermissionStrategy()),
    CHECK_WEEK_TODO(new CheckWeekTodoPermissionStrategy()),;

    private final UpdateCharacterStrategy strategy;

    FriendPermissionType(UpdateCharacterStrategy strategy) {
        this.strategy = strategy;
    }

    public void validate(Friends friend) {
        strategy.validatePermission(friend);
    }
}
