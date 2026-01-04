package lostark.todo.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PerformanceExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String START_TIME = "startTime";
    private static final String MEMORY_BEFORE = "memoryBefore";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        QueryCounter.reset();

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        ExtensionContext.Store store = getStore(context);
        store.put(START_TIME, System.currentTimeMillis());
        store.put(MEMORY_BEFORE, runtime.totalMemory() - runtime.freeMemory());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ExtensionContext.Store store = getStore(context);
        long startTime = store.remove(START_TIME, Long.class);
        long memoryBefore = store.remove(MEMORY_BEFORE, Long.class);

        long duration = System.currentTimeMillis() - startTime;
        Runtime runtime = Runtime.getRuntime();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (memoryAfter - memoryBefore) / 1024;
        int queryCount = QueryCounter.getCount();

        log.info("실행 시간: {}ms | 메모리: {}KB | 쿼리 수: {}", duration, memoryUsed, queryCount);

        // maxQueries 검증
        context.getTestMethod().ifPresent(method -> {
            MeasurePerformance annotation = method.getAnnotation(MeasurePerformance.class);
            if (annotation != null && annotation.maxQueries() < Integer.MAX_VALUE) {
                assertThat(queryCount)
                        .as("쿼리 수가 %d개를 초과했습니다", annotation.maxQueries())
                        .isLessThanOrEqualTo(annotation.maxQueries());
            }
        });
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
