package lostark.todo.domainMyGame.mygame.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
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
class MyGameApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게임 목록 조회")
    @MeasurePerformance(maxQueries = 5)
    void getGames() throws Exception {
        mockMvc.perform(get("/api/v1/games"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("전체 게임 목록 조회")
    @MeasurePerformance(maxQueries = 5)
    void getAllGames() throws Exception {
        mockMvc.perform(get("/api/v1/games/all"))
                .andExpect(status().isOk());
    }
}
