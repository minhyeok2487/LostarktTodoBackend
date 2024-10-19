package lostark.todo.domainV2.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FriendFindCharacterResponse {

    private long id;

    private String username;

    private String characterName;

    private int characterListSize;

    private String areWeFriend;
}
