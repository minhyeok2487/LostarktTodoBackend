package lostark.todo.controller.dtoV2.recruitingBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

import java.time.LocalDateTime;

@Data
public class SearchRecruitingBoardResponse {

    @ApiModelProperty(example = "모집 게시글 Id")
    private long recruitingBoardId;

    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;

    @ApiModelProperty(example = "제목")
    private String title;

    @ApiModelProperty(example = "내용")
    private String body;

    @ApiModelProperty(example = "작성자 캐릭터 이름")
    private String mainCharacterName;

    @ApiModelProperty(example = "글 생성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(example = "조회수")
    private int showCount;

    public SearchRecruitingBoardResponse(RecruitingBoard recruitingBoard) {
        this.recruitingBoardId = recruitingBoard.getId();
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
