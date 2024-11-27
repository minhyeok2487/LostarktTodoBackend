package lostark.todo.controller.dto.friendsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.friend.entity.FriendSettings;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FriendsReturnDto {

    private long id;

    private String friendUsername;

    private String areWeFriend;

    private String nickName;

    private List<CharacterDto> characterList;

    private FriendSettings toFriendSettings;

    private FriendSettings fromFriendSettings;
}
