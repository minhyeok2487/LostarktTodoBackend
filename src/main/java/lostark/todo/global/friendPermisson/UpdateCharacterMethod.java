package lostark.todo.global.friendPermisson;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.friend.service.FriendsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateCharacterMethod {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @Transactional(readOnly = true)
    public Character getUpdateCharacter(String username, String friendUsername, long characterId, FriendPermissionType permissionType) {
        if (friendUsername == null) {
            return characterService.get(characterId, username);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            permissionType.validate(friend);
            return characterService.get(characterId, friendUsername);
        }
    }
}
