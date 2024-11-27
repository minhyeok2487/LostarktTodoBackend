package lostark.todo.domain.friend.enums;

import lombok.Getter;

@Getter
public enum FriendRequestCategory {

    OK("수락"),
    REJECT("거부"),
    DELETE("삭제");

    private final String type;

    FriendRequestCategory(String type) {
        this.type = type;
    }

}
