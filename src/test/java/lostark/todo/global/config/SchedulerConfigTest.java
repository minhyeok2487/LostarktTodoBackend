package lostark.todo.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerConfigTest {

    @Test
    @DisplayName("SchedulingConfigurer를 구현한다")
    void shouldImplementSchedulingConfigurer() {
        SchedulerConfig config = new SchedulerConfig();
        assertThat(config).isInstanceOf(SchedulingConfigurer.class);
    }

    @Test
    @DisplayName("TaskScheduler 빈이 ThreadPoolTaskScheduler 타입이고 core pool size가 3이다")
    void taskSchedulerBean_shouldHaveCorrectPoolSize() {
        SchedulerConfig config = new SchedulerConfig();
        TaskScheduler scheduler = config.taskScheduler();

        assertThat(scheduler).isInstanceOf(ThreadPoolTaskScheduler.class);

        ThreadPoolTaskScheduler threadPool = (ThreadPoolTaskScheduler) scheduler;
        assertThat(threadPool.getScheduledThreadPoolExecutor().getCorePoolSize()).isEqualTo(3);
    }
}
