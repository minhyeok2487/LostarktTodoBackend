package lostark.todo.controller.dtoV2.recruitingBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

@Data
public class SearchRecruitingBoardRequest {

    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;
}
