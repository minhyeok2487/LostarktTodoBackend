package lostark.todo.domain.character.constants;

public final class ElysianConstants {
    public static final int MAX_ELYSIAN_COUNT = 5;
    public static final String MAX_ELYSIAN_ERROR_MESSAGE = String.format("낙원(천상)은 일주일에 %d 번만 돌 수 있어요.", MAX_ELYSIAN_COUNT);
    public static final int MIN_ELYSIAN_COUNT = 0;
    public static final String MIN_ELYSIAN_ERROR_MESSAGE = String.format("%d 이하의 숫자로 변경할 수 없습니다.", MIN_ELYSIAN_COUNT);


    public enum UpdateElysianActionEnum {
        INCREMENT, DECREMENT
    }

    // 인스턴스 생성 방지
    private ElysianConstants() {}
}
