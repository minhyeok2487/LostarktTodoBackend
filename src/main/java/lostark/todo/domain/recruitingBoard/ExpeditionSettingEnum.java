package lostark.todo.domain.recruitingBoard;

public enum ExpeditionSettingEnum {

    MAIN_CHARACTER("메인 캐릭터 아이템 레벨"),
    AVG_GOLD_CHARACTER("골드 획득 캐릭터 아이템 레벨 평균"),
    AVG_ALL_CHARACTER("전체 캐릭터 아이템 레벨 평균"),
    NONE("미공개");

    private final String description;

    ExpeditionSettingEnum(String description) {
        this.description = description;
    }
}
