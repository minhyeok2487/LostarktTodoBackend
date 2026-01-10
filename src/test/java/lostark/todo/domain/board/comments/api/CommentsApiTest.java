package lostark.todo.domain.board.comments.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.global.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
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
class CommentsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("방명록 조회 - 기본 페이지")
    @MeasurePerformance(maxQueries = 15)
    void searchComments() throws Exception {
        // TODO: 쿼리 12개 발생 - N+1 문제 의심, 최적화 필요
        mockMvc.perform(get("/api/v1/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("방명록 조회 - 페이지 파라미터")
    @MeasurePerformance(maxQueries = 15)
    void searchCommentsWithPage() throws Exception {
        mockMvc.perform(get("/api/v1/comments")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "10"))
                .andExpect(status().isOk());
    }
}
