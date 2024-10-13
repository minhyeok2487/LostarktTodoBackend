package lostark.todo.global.updateCharacter;

import lostark.todo.domain.friends.Friends;

public interface UpdateCharacterStrategy {
    void validatePermission(Friends friend);
}
