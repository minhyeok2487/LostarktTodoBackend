package lostark.todo.domainV2.board.recrutingBoard.enums;

public enum RecruitingCategoryEnum {

    FRIENDS("깐부 구해요"),
    RECRUITING_GUILD("길드원 모집"),
    LOOKING_GUILD("길드 구해요"),
    RECRUITING_PARTY("고정팟 모집"),
    LOOKING_PARTY("고정팟 구해요"),
    ETC("기타");

    private final String description;

    RecruitingCategoryEnum(String description) {
        this.description = description;
    }
}
