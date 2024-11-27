package lostark.todo.domain.board.community.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowingUpdateRequest {

    @ApiModelProperty(notes = "팔로우 사용자 id")
    private long following;
}
