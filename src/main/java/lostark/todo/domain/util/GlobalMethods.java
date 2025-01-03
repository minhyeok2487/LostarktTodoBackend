package lostark.todo.domain.util;

public class GlobalMethods {

    // 조건에 맞으면 action 실행
    public static void checkAndUpdate(boolean condition, Runnable action) {
        if (condition) {
            action.run();
        }
    }
}
