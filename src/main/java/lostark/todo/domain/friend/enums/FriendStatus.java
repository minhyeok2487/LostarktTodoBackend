package lostark.todo.domain.friend.enums;

import lombok.Getter;

@Getter
public enum FriendStatus {

    FRIEND("깐부"),
    FRIEND_PROGRESSING("깐부 요청 진행중"),
    FRIEND_RECEIVED("깐부 요청 받음"),
    FRIEND_REJECT("깐부 요청 거부"),
    FRIEND_SEND("깐부 요청");

    private final String type;

    FriendStatus(String type) {
        this.type = type;
    }

}
