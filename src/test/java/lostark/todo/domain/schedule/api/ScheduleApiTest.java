package lostark.todo.domain.schedule.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.schedule.entity.Schedule;
import lostark.todo.domain.schedule.enums.ScheduleCategory;
import lostark.todo.domain.schedule.enums.ScheduleRaidCategory;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
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

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Autowired
    private ScheduleRepository scheduleRepository;

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

    @Test
    @DisplayName("일정 자세히 보기")
    @MeasurePerformance(maxQueries = 10)
    void getSchedule() throws Exception {
        Schedule schedule = createTestSchedule();

        mockMvc.perform(get("/api/v1/schedule/{scheduleId}", schedule.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일정 수정")
    @MeasurePerformance(maxQueries = 15)
    void edit() throws Exception {
        Schedule schedule = createTestSchedule();

        Map<String, Object> request = new HashMap<>();
        request.put("dayOfWeek", "TUESDAY");
        request.put("time", "20:00");
        request.put("memo", "메모 수정");
        request.put("autoCheck", true);

        mockMvc.perform(patch("/api/v1/schedule/{scheduleId}", schedule.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일정 삭제")
    @MeasurePerformance(maxQueries = 10)
    void remove() throws Exception {
        Schedule schedule = createTestSchedule();

        mockMvc.perform(delete("/api/v1/schedule/{scheduleId}", schedule.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private Schedule createTestSchedule() {
        return scheduleRepository.save(Schedule.builder()
                .characterId(testCharacter.getId())
                .scheduleRaidCategory(ScheduleRaidCategory.RAID)
                .scheduleCategory(ScheduleCategory.ALONE)
                .raidName("테스트 레이드")
                .dayOfWeek(DayOfWeek.MONDAY)
                .time(LocalTime.of(19, 0))
                .repeatWeek(true)
                .leader(true)
                .leaderScheduleId(0L)
                .autoCheck(true)
                .build());
    }
}
