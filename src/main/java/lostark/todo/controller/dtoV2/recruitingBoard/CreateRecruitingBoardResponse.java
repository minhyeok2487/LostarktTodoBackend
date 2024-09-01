package lostark.todo.controller.dtoV2.recruitingBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateRecruitingBoardResponse {

    @ApiModelProperty(example = "모집 게시글 Id")
    private long recruitingBoardId;

    public CreateRecruitingBoardResponse(long id) {
        this.recruitingBoardId = id;
    }
}
