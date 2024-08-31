package lostark.todo.controller.dtoV2.recruitingBoard;

import lombok.Data;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

import java.time.LocalDateTime;

@Data
public class SearchRecruitingBoardResponse {

    private long id;

    private RecruitingCategoryEnum recruitingCategory;

    private String title;

    private String body;

    private String mainCharacterName;

    private LocalDateTime createdDate;

    private int showCount;

    public SearchRecruitingBoardResponse(RecruitingBoard recruitingBoard) {
        this.id = recruitingBoard.getId();
        this.recruitingCategory = recruitingBoard.getRecruitingCategory();
        this.title = recruitingBoard.getTitle();
        this.body = recruitingBoard.getBody();
        this.createdDate = recruitingBoard.getCreatedDate();
        this.showCount = recruitingBoard.getShowCount();
        if (recruitingBoard.isShowMainCharacter()) {
            this.mainCharacterName = recruitingBoard.getMember().getMainCharacter() != null ?
                    recruitingBoard.getMember().getMainCharacter() :
                    recruitingBoard.getMember().getCharacters().get(0).getCharacterName();
        } else {
            this.mainCharacterName = "비공개";
        }
    }
}
