package lostark.todo.domain.character.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CharacterDayApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("단일 캐릭터 일일컨텐츠 전체 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateDayCheckAll() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/character/day/check/all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("전체 캐릭터 일일컨텐츠 전체 체크")
    @MeasurePerformance(maxQueries = 30)
    void updateDayCheckAllCharacters() throws Exception {
        // TODO: 다수 쿼리 발생 가능성
        Map<String, Object> request = new HashMap<>();
        request.put("serverName", testCharacter.getServerName());

        mockMvc.perform(post("/api/v1/character/day/check/all-characters")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 컨텐츠 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateDayCheck() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("allCheck", false);
        request.put("category", "epona");

        mockMvc.perform(post("/api/v1/character/day/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 컨텐츠 휴식게이지 업데이트")
    @MeasurePerformance(maxQueries = 10)
    void updateDayGauge() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("chaosGauge", 20);
        request.put("guardianGauge", 20);

        mockMvc.perform(post("/api/v1/character/day/gauge")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
