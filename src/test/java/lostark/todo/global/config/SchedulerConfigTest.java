package lostark.todo.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerConfigTest {

    @Test
    @DisplayName("SchedulingConfigurer를 구현한다")
    void shouldImplementSchedulingConfigurer() {
        SchedulerConfig config = new SchedulerConfig();
        assertThat(config).isInstanceOf(SchedulingConfigurer.class);
    }

    @Test
    @DisplayName("TaskScheduler 빈이 ThreadPoolTaskScheduler 타입이고 pool size가 3이다")
    void taskSchedulerBean_shouldHaveCorrectPoolSize() {
        SchedulerConfig config = new SchedulerConfig();
        TaskScheduler scheduler = config.taskScheduler();

        assertThat(scheduler).isInstanceOf(ThreadPoolTaskScheduler.class);

        ThreadPoolTaskScheduler threadPool = (ThreadPoolTaskScheduler) scheduler;
        int poolSize = (int) ReflectionTestUtils.getField(threadPool, "poolSize");
        assertThat(poolSize).isEqualTo(3);
    }

    @Test
    @DisplayName("스레드 이름 prefix가 scheduled- 이다")
    void taskScheduler_shouldHaveCorrectThreadNamePrefix() {
        SchedulerConfig config = new SchedulerConfig();
        ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) config.taskScheduler();

        String prefix = (String) ReflectionTestUtils.getField(scheduler, "threadNamePrefix");
        assertThat(prefix).isEqualTo("scheduled-");
    }
}
