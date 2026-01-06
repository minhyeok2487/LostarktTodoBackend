package lostark.todo.domain.character.api;

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
class CharacterListApiTest {

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
    @DisplayName("캐릭터 리스트 조회")
    @MeasurePerformance(maxQueries = 30)
    void get_characterList() throws Exception {
        // TODO: 다수 쿼리 발생 - N+1 최적화 필요
        mockMvc.perform(get("/api/v1/character-list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("삭제된 캐릭터 리스트 조회")
    @MeasurePerformance(maxQueries = 5)
    void getDeletedCharacter() throws Exception {
        mockMvc.perform(get("/api/v1/character-list/deleted")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
