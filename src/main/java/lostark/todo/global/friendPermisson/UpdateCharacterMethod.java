package lostark.todo.global.friendPermisson;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.friend.entity.Friends;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.domainV2.friend.service.FriendsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateCharacterMethod {

    private final CharacterService characterService;
    private final FriendsService friendsService;

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
