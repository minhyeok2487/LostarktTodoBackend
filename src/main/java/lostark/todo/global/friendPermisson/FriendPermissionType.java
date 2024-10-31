package lostark.todo.global.friendPermisson;

import lostark.todo.domainV2.friend.entity.Friends;

public enum FriendPermissionType {

    UPDATE_RAID(new UpdateRaidPermissionStrategy()),
    CHECK_RAID(new CheckRaidPermissionStrategy()),
    CHECK_WEEK_TODO(new CheckWeekTodoPermissionStrategy()),
    CHECK_DAY_TODO(new CheckDayTodoPermissionStrategy()),
    UPDATE_GAUGE(new UpdateGaugePermissionStrategy());

    private final UpdateCharacterStrategy strategy;

    FriendPermissionType(UpdateCharacterStrategy strategy) {
        this.strategy = strategy;
    }

    public void validate(Friends friend) {
        strategy.validatePermission(friend);
    }
}
