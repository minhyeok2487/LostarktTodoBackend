package lostark.todo.domain.board.community.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.board.community.entity.CommunityCategory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CommunitySaveRequest {

    @NotEmpty
    private String body;

    @NotNull
    private CommunityCategory category;

    @NotNull
    private boolean showName;

    private List<Long> imageList;

    @ApiModelProperty(notes = "게시글 작성이면 0, 게시글의 댓글이면 게시글 ID")
    private long rootParentId;

    @ApiModelProperty(notes = "댓글의 댓글이면 상위 댓글 ID, 아니면 0")
    private long commentParentId;
}
