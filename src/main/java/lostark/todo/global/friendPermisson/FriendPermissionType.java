package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friends.Friends;

public enum FriendPermissionType {

    UPDATE_RAID(new UpdateRaidPermissionStrategy()),
    CHECK_WEEK_TODO(new CheckWeekTodoPermissionStrategy()),
    CHECK_DAY_TODO(new CheckWeekTodoPermissionStrategy()),
    UPDATE_GAUGE(new UpdateGaugePermissionStrategy());

    private final UpdateCharacterStrategy strategy;

    FriendPermissionType(UpdateCharacterStrategy strategy) {
        this.strategy = strategy;
    }

    public void validate(Friends friend) {
        strategy.validatePermission(friend);
    }
}
