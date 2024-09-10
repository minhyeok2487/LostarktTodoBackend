package lostark.todo.domainV2.board.recrutingBoard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.recrutingBoard.enums.ExpeditionSettingEnum;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;
import lostark.todo.domainV2.board.recrutingBoard.enums.TimeCategoryEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetRecruitingBoardResponse {

    @ApiModelProperty(example = "모집 게시글 Id")
    private long recruitingBoardId;

    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;

    @ApiModelProperty(example = "제목")
    private String title;

    @ApiModelProperty(example = "내용")
    private String body;

    @ApiModelProperty(example = "주중 플레이 시간")
    private List<TimeCategoryEnum> weekdaysPlay;

    @ApiModelProperty(example = "주말/공휴일 플레이 시간")
    private List<TimeCategoryEnum> weekendsPlay;

    @ApiModelProperty(example = "외부 링크 리스트")
    private List<String> url;

    @ApiModelProperty(example = "글 생성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(example = "조회수")
    private int showCount;

    @ApiModelProperty(example = "작성자 캐릭터 이름")
    private String mainCharacterName;

    @ApiModelProperty(example = "아이템 레벨 표기 설정")
    private ExpeditionSettingEnum expeditionSetting;

    @ApiModelProperty(example = "아이템 레벨")
    private double itemLevel;

    @ApiModelProperty(example = "게시글 삭제 가능 여부")
    private boolean authDelete;

    public GetRecruitingBoardResponse(RecruitingBoard recruitingBoard, Member member) {
        this.recruitingBoardId = recruitingBoard.getId();
        this.recruitingCategory = recruitingBoard.getRecruitingCategory();
        this.title = recruitingBoard.getTitle();
        this.body = recruitingBoard.getBody();
        this.createdDate = recruitingBoard.getCreatedDate();
        this.showCount = recruitingBoard.getShowCount();
        this.weekdaysPlay = recruitingBoard.createWeekdaysPlay();
        this.weekendsPlay = recruitingBoard.createWeekendsPlay();
        this.url = recruitingBoard.createUrlList();
        this.expeditionSetting = recruitingBoard.getExpeditionSetting();
        this.mainCharacterName = recruitingBoard.getDisplayCharacterName();

        String mainCharacter = recruitingBoard.determineMainCharacter();
        this.itemLevel = recruitingBoard.calculateDisplayItemLevel(mainCharacter);

        this.authDelete = recruitingBoard.checkAuthDelete(member);
    }


}
