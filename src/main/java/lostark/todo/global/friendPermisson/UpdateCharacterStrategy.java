package lostark.todo.global.friendPermisson;

import lostark.todo.domain.friends.Friends;

public interface UpdateCharacterStrategy {
    void validatePermission(Friends friend);
}
