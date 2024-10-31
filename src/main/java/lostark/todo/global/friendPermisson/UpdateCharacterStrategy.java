package lostark.todo.global.friendPermisson;

import lostark.todo.domainV2.friend.entity.Friends;

public interface UpdateCharacterStrategy {
    void validatePermission(Friends friend);
}
