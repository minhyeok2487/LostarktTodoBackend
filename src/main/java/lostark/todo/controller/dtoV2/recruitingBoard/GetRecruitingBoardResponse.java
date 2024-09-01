package lostark.todo.controller.dtoV2.recruitingBoard;

import lombok.Data;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingCategoryEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetRecruitingBoardResponse {

    private long id;

    private RecruitingCategoryEnum recruitingCategory;

    private String title;

    private String body;

    private String weekdaysPlay;

    private String weekendsPlay;

    private String url1;

    private String url2;

    private String url3;

    private LocalDateTime createdDate;

    private int showCount;

    private String mainCharacterName;

    private double itemLevel;

    public GetRecruitingBoardResponse(RecruitingBoard recruitingBoard) {
        this.id = recruitingBoard.getId();
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
