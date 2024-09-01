package lostark.todo.controller.dtoV2.recruitingBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.recruitingBoard.ExpeditionSettingEnum;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;
import lostark.todo.domain.recruitingBoard.TimeCategoryEnum;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateRecruitingBoardRequest {

    @NotNull
    @ApiModelProperty(example = "제목")
    private String title;

    @NotNull
    @ApiModelProperty(example = "메인 캐릭터 닉네임 공개/비공개")
    private Boolean showMainCharacter;

    @NotNull
    @ApiModelProperty(example = "아이템 레벨 표기 설정")
    private ExpeditionSettingEnum expeditionSetting;

    @NotNull
    @ApiModelProperty(example = "주중 플레이 시간")
    private List<TimeCategoryEnum> weekdaysPlay;

    @NotNull
    @ApiModelProperty(example = "주말/공휴일 플레이 시간")
    private List<TimeCategoryEnum> weekendsPlay;

    @NotNull
    @ApiModelProperty(example = "게시판 카테고리")
    private RecruitingCategoryEnum recruitingCategory;

    @ApiModelProperty(example = "내용")
    private String body;

    @ApiModelProperty(example = "외부 링크 1")
    private String url1;

    @ApiModelProperty(example = "외부 링크 2")
    private String url2;

    @ApiModelProperty(example = "외부 링크 3")
    private String url3;

    @ApiModelProperty(notes = "이미지 name 리스트")
    private List<String> fileNames;
}
