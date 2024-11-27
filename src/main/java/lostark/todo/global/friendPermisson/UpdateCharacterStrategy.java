package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friend.entity.Friends;

public interface UpdateCharacterStrategy {
    void validatePermission(Friends friend);
}
