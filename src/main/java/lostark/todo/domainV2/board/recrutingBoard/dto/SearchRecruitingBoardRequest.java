package lostark.todo.domainV2.board.recrutingBoard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;

@Data
public class SearchRecruitingBoardRequest {

    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;
}
