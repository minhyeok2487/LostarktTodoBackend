package lostark.todo.controller.dtoV2.firend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.friends.FriendSettings;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FriendsResponse {

    private long friendId;

    private String friendUsername;

    private String areWeFriend;

    private String nickName;

    private int ordering;

    private List<CharacterResponse> characterList;

    private FriendSettings toFriendSettings;

    private FriendSettings fromFriendSettings;
}
