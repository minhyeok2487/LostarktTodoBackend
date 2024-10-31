package lostark.todo.domainV2.board.community.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CommunityUpdateRequest {

    @NotNull
    private long communityId;

    @NotEmpty
    private String body;
}
