package lostark.todo.global.updateCharacter;

import lostark.todo.domain.friends.Friends;

public enum RaidPermissionType {

    UPDATE_RAID(new UpdateRaidPermissionStrategy());

    private final UpdateCharacterStrategy strategy;

    RaidPermissionType(UpdateCharacterStrategy strategy) {
        this.strategy = strategy;
    }

    public void validate(Friends friend) {
        strategy.validatePermission(friend);
    }
}
