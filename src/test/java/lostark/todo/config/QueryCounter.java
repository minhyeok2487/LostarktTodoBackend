package lostark.todo.config;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQL 쿼리 횟수를 카운트하는 유틸 클래스
 * ThreadLocal을 사용하여 병렬 테스트 환경에서도 스레드별로 격리된 카운팅 지원
 */
public class QueryCounter {

    private static final ThreadLocal<AtomicInteger> count = ThreadLocal.withInitial(AtomicInteger::new);

    public static void reset() {
        count.get().set(0);
    }

    public static void increment() {
        count.get().incrementAndGet();
    }

    public static int getCount() {
        return count.get().get();
    }

    /**
     * ThreadLocal 값을 정리하여 메모리 누수를 방지합니다.
     * 테스트 종료 시 반드시 호출해야 합니다.
     */
    public static void clear() {
        count.remove();
    }
}
