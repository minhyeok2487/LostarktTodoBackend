package lostark.todo.controller.dtoV2.recruitingBoard;

import lombok.Data;
import lostark.todo.domain.recruitingBoard.ExpeditionSettingEnum;
import lostark.todo.domain.recruitingBoard.TimeCategoryEnum;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateRecruitingBoardRequest {

    @NotNull
    private String title;

    @NotNull
    private Boolean showMainCharacter;

    @NotNull
    private ExpeditionSettingEnum expeditionSetting;

    @NotNull
    private List<TimeCategoryEnum> weekdaysPlay;

    @NotNull
    private List<TimeCategoryEnum> weekendsPlay;

    private String body;

    private String url1;

    private String url2;

    private String url3;
}
