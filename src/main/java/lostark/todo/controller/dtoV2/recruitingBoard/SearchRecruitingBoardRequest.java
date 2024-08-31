package lostark.todo.controller.dtoV2.recruitingBoard;

import lombok.Data;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

@Data
public class SearchRecruitingBoardRequest {

    private RecruitingCategoryEnum recruitingCategory;
}
