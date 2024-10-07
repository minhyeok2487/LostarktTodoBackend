package lostark.todo.controller.dto.friendsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindCharacterWithFriendsDto {

    private long id;

    private String username;

    private String characterName;

    private int characterListSize;

    private String areWeFriend;
}
