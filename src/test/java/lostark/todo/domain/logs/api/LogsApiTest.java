package lostark.todo.domain.logs.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.logs.entity.Logs;
import lostark.todo.domain.logs.enums.LogContent;
import lostark.todo.domain.logs.enums.LogType;
import lostark.todo.domain.logs.repository.LogsRepository;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class LogsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogsRepository logsRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
        testCharacter = testMember.getCharacters().stream()
                .filter(c -> !c.isDeleted())
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("최근 로그 조회")
    @MeasurePerformance(maxQueries = 10)
    void search() throws Exception {
        mockMvc.perform(get("/api/v1/logs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("기타 수익 추가")
    @MeasurePerformance(maxQueries = 10)
    void saveEtcLog() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("localDate", LocalDate.now().toString());
        request.put("message", "테스트 수익");
        request.put("profit", 10000.0);

        mockMvc.perform(post("/api/v1/logs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteLog() throws Exception {
        Logs log = logsRepository.save(Logs.builder()
                .localDate(LocalDate.now())
                .memberId(testMember.getId())
                .characterId(testCharacter.getId())
                .logType(LogType.ETC)
                .logContent(LogContent.ETC)
                .name(testCharacter.getCharacterName())
                .message("테스트 로그")
                .profit(10000.0)
                .deleted(false)
                .build());

        mockMvc.perform(delete("/api/v1/logs/{logId}", log.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
