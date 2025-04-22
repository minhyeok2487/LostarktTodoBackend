package lostark.todo.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.friend.entity.FriendSettings;

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
