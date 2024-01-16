package lostark.todo.event.entity.member;

import lombok.Data;

public enum MemberEventType {

    signUp("1차 회원가입", "1차 회원가입이 정상 처리되었습니다."),
    addCharacters("캐릭터 추가", "캐릭터 추가가 정상 처리되었습니다.");

    private final String type;

    private final String message;

    MemberEventType(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
