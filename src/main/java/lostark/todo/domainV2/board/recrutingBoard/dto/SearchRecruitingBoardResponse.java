package lostark.todo.domainV2.board.recrutingBoard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;

import java.time.LocalDateTime;

@Data
public class SearchRecruitingBoardResponse {

    @ApiModelProperty(example = "모집 게시글 Id")
    private long recruitingBoardId;

    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;

    @ApiModelProperty(example = "제목")
    private String title;

    @ApiModelProperty(example = "작성자 캐릭터 이름")
    private String mainCharacterName;

    @ApiModelProperty(example = "작성자 아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(example = "글 작성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(example = "조회수")
    private int showCount;

    public SearchRecruitingBoardResponse(RecruitingBoard recruitingBoard) {
        this.recruitingBoardId = recruitingBoard.getId();
        this.recruitingCategory = recruitingBoard.getRecruitingCategory();
        this.title = recruitingBoard.getTitle();
        this.createdDate = recruitingBoard.getCreatedDate();
        this.showCount = recruitingBoard.getShowCount();
        this.mainCharacterName = recruitingBoard.getDisplayCharacterName();

        String mainCharacter = recruitingBoard.determineMainCharacter();
        this.itemLevel = recruitingBoard.calculateDisplayItemLevel(mainCharacter);
    }
}
