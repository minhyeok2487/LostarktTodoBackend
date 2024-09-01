package lostark.todo.controller.dtoV2.recruitingBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.recruitingBoard.ExpeditionSettingEnum;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

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
    private String weekdaysPlay;

    @ApiModelProperty(example = "주말/공휴일 플레이 시간")
    private String weekendsPlay;

    @ApiModelProperty(example = "외부 링크 1")
    private String url1;

    @ApiModelProperty(example = "외부 링크 2")
    private String url2;

    @ApiModelProperty(example = "외부 링크 3")
    private String url3;

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

    public GetRecruitingBoardResponse(RecruitingBoard recruitingBoard) {
        this.recruitingBoardId = recruitingBoard.getId();
        this.recruitingCategory = recruitingBoard.getRecruitingCategory();
        this.title = recruitingBoard.getTitle();
        this.body = recruitingBoard.getBody();
        this.createdDate = recruitingBoard.getCreatedDate();
        this.showCount = recruitingBoard.getShowCount();
        this.weekdaysPlay = recruitingBoard.getWeekdaysPlay();
        this.weekendsPlay = recruitingBoard.getWeekendsPlay();
        this.url1 = recruitingBoard.getUrl1();
        this.url2 = recruitingBoard.getUrl2();
        this.url3 = recruitingBoard.getUrl3();
        this.expeditionSetting = recruitingBoard.getExpeditionSetting();

        String mainCharacter = determineMainCharacter(recruitingBoard);
        this.mainCharacterName = recruitingBoard.isShowMainCharacter() ? mainCharacter : null;

        this.itemLevel = calculateItemLevel(recruitingBoard, mainCharacter);
    }

    private String determineMainCharacter(RecruitingBoard recruitingBoard) {
        return recruitingBoard.getMember().getMainCharacter() != null
                ? recruitingBoard.getMember().getMainCharacter()
                : recruitingBoard.getMember().getCharacters().stream()
                .findFirst()
                .map(Character::getCharacterName)
                .orElse(null);
    }

    private double calculateItemLevel(RecruitingBoard recruitingBoard, String mainCharacter) {
        List<Character> characters = recruitingBoard.getMember().getCharacters();

        return switch (recruitingBoard.getExpeditionSetting()) {
            case MAIN_CHARACTER -> getMainCharacterItemLevel(characters, mainCharacter);
            case AVG_GOLD_CHARACTER -> calculateAverageGoldCharacterItemLevel(characters);
            case AVG_ALL_CHARACTER -> calculateAverageItemLevel(characters);
            default -> 0.0;
        };
    }

    private double getMainCharacterItemLevel(List<Character> characters, String mainCharacter) {
        return characters.stream()
                .filter(character -> character.getCharacterName().equals(mainCharacter))
                .findFirst()
                .map(Character::getItemLevel)
                .orElse(0.0);
    }

    private double calculateAverageGoldCharacterItemLevel(List<Character> characters) {
        return calculateAverageItemLevel(
                characters.stream().filter(Character::isGoldCharacter).toList()
        );
    }

    private double calculateAverageItemLevel(List<Character> characters) {
        return characters.stream()
                .mapToDouble(Character::getItemLevel)
                .average()
                .orElse(0.0);
    }
}
