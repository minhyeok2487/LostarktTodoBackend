package lostark.todo.event.entity;

public enum EventType {

    signUp("1차 회원가입", "1차 회원가입이 정상 처리되었습니다."),
    addCharacters("캐릭터 추가", "캐릭터 추가가 정상 처리되었습니다."),

    checkDayContent("일일 숙제", "일일 숙제 체크 값이 변경 되었습니다.");

    private final String type;

    private final String message;

    EventType(String type, String message) {
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
