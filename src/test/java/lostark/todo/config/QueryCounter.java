package lostark.todo.config;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQL 쿼리 횟수를 카운트하는 유틸 클래스
 */
public class QueryCounter {

    private static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static void increment() {
        count.incrementAndGet();
    }

    public static int getCount() {
        return count.get();
    }
}
