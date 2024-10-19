package lostark.todo.controller.dto.friendsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateFriendSettingRequest {

    private long id;

    private boolean value;

    private String name;
}
