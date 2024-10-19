package lostark.todo.domainV2.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainV2.friend.enums.FriendRequestCategory;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateFriendRequest {

    private String friendUsername;

    private FriendRequestCategory category;
}
