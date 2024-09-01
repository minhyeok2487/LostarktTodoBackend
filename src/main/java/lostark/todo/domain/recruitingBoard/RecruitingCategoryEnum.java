package lostark.todo.domain.recruitingBoard;

public enum RecruitingCategoryEnum {

    FRIENDS("깐부 구해요"),
    RECRUITING_GUILD("길드원 모집"),
    LOOKING_GUILD("길드 구해요"),
    RECRUITING_PARTY("공격대 모집"),
    LOOKING_PARTY("공격대 구해요"),
    ETC("기타");

    private final String description;

    RecruitingCategoryEnum(String description) {
        this.description = description;
    }
}
