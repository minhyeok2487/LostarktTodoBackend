package lostark.todo.domain.board.community.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.board.community.repository.CommunityRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CommunityApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CommunityRepository communityRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("커뮤니티 카테고리 조회")
    @MeasurePerformance(maxQueries = 1)
    void getCommunityCategory() throws Exception {
        mockMvc.perform(get("/api/v1/community/category")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 목록 조회")
    @MeasurePerformance(maxQueries = 30)
    void search() throws Exception {
        // TODO: 쿼리 23개 발생 - N+1 최적화 필요
        mockMvc.perform(get("/api/v1/community")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 상세 조회")
    @MeasurePerformance(maxQueries = 10)
    void getDetail() throws Exception {
        Community community = communityRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (community != null) {
            mockMvc.perform(get("/api/v1/community/{communityId}", community.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }
}
