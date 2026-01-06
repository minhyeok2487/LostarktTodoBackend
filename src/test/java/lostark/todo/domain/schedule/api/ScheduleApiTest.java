package lostark.todo.domain.schedule.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("월별 일정 조회 - N+1 문제 해결 검증")
    @MeasurePerformance(maxQueries = 3)
    void search_noNPlusOne() throws Exception {
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
        mockMvc.perform(get("/api/v1/schedule/raid/category")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일정 생성")
    @MeasurePerformance(maxQueries = 15)
    void create() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("scheduleCategory", "ALONE");
        request.put("scheduleRaidCategory", "RAID");
        request.put("raidName", "발탄");
        request.put("dayOfWeek", "MONDAY");
        request.put("time", "19:00");
        request.put("repeatWeek", false);
        request.put("leaderCharacterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/schedule")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
