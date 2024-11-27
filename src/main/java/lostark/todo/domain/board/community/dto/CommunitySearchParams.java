package lostark.todo.domain.board.community.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.board.community.entity.CommunityCategory;

@Data
public class CommunitySearchParams {

    @ApiModelProperty(name = "커뮤니티 게시글 ID, 첫 글이면 X")
    private Long communityId;

    @ApiModelProperty(name = "커뮤니티 카테고리, 없으면 X")
    private CommunityCategory category;
}
