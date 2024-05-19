package lostark.todo.domain.friends;

import lostark.todo.domain.character.Character;

import java.util.Optional;

public interface FriendsCustomRepository {
    Character findFriendCharacter(String friendUsername, long characterId);

    Optional<Friends> findByFriendUsername(String friendUsername, String username);
}
