package lostark.todo.domain.friend.dto;

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
