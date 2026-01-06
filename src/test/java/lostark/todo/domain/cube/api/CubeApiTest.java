package lostark.todo.domain.cube.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.cube.entity.Cubes;
import lostark.todo.domain.cube.repository.CubesRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CubeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CubesRepository cubesRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        Member member = memberService.get(TEST_USERNAME);
        testCharacter = member.getCharacters().stream()
                .filter(c -> !c.isDeleted())
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("큐브 통계 데이터 조회")
    @MeasurePerformance(maxQueries = 5)
    void getStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/cube/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("큐브 컨텐츠 조회")
    @MeasurePerformance(maxQueries = 10)
    void getCubeContents() throws Exception {
        mockMvc.perform(get("/api/v1/cube")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("큐브 컨텐츠 추가")
    @MeasurePerformance(maxQueries = 10)
    void create() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/cube")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("큐브 티켓 숫자 변경")
    @MeasurePerformance(maxQueries = 10)
    void update() throws Exception {
        Cubes cubes = createTestCubes();

        Map<String, Object> request = new HashMap<>();
        request.put("cubeId", cubes.getId());
        request.put("characterId", testCharacter.getId());
        request.put("ban1", 5);
        request.put("ban2", 3);
        request.put("ban3", 2);
        request.put("ban4", 1);
        request.put("ban5", 0);
        request.put("unlock1", 4);
        request.put("unlock2", 2);
        request.put("unlock3", 1);
        request.put("unlock4", 0);

        mockMvc.perform(put("/api/v1/cube")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("큐브 컨텐츠 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteCube() throws Exception {
        createTestCubes();

        mockMvc.perform(delete("/api/v1/cube/{characterId}", testCharacter.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("큐브 티켓 소모")
    @MeasurePerformance(maxQueries = 15)
    void spendWeekCubeTicket() throws Exception {
        Cubes cubes = createTestCubes();
        cubes.setBan1(3);
        cubesRepository.save(cubes);

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("cubeContentName", "BAN_1");

        mockMvc.perform(post("/api/v1/cube/spend")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private Cubes createTestCubes() {
        return cubesRepository.save(Cubes.toEntity(testCharacter.getId()));
    }
}
