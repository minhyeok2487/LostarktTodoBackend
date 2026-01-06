package lostark.todo.domain.schedule.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.global.config.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class ScheduleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    @Test
    @DisplayName("월별 일정 조회 - N+1 문제 해결 검증")
    @MeasurePerformance(maxQueries = 3)
    void search_noNPlusOne() throws Exception {
        String token = tokenProvider.createToken(TEST_USERNAME);

        mockMvc.perform(get("/api/v1/schedule")
                        .header("Authorization", "Bearer " + token)
                        .param("year", "2026")
                        .param("month", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("레이드 카테고리 조회")
    @MeasurePerformance(maxQueries = 5)
    void getScheduleRaidCategory() throws Exception {
        String token = tokenProvider.createToken(TEST_USERNAME);

        mockMvc.perform(get("/api/v1/schedule/raid/category")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
