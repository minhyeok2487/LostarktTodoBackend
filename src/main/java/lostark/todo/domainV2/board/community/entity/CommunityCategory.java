package lostark.todo.domainV2.board.community.entity;

import lombok.Getter;

@Getter
public enum CommunityCategory {

    LIFE("일상"),
    FRIENDS("깐부모집"),
    GUILDS("길드모집"),
    PARTIES("고정팟모집"),
    BOARDS("로투두공지"),
    COMMENTS("로투두건의사항");

    private final String categoryName;

    CommunityCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
