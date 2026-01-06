package lostark.todo.domain.character.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.CustomTodo;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CustomTodoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CustomTodoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomTodoRepository customTodoRepository;

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
    @DisplayName("커스텀 숙제 조회")
    @MeasurePerformance(maxQueries = 10)
    void search() throws Exception {
        mockMvc.perform(get("/api/v1/custom")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("커스텀 숙제 추가")
    @MeasurePerformance(maxQueries = 15)
    void create() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("contentName", "테스트 숙제");
        request.put("frequency", CustomTodoFrequencyEnum.DAILY.name());

        mockMvc.perform(post("/api/v1/custom")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("커스텀 숙제 수정")
    @MeasurePerformance(maxQueries = 15)
    void update() throws Exception {
        CustomTodo customTodo = createTestCustomTodo();

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("contentName", "수정된 숙제");

        mockMvc.perform(patch("/api/v1/custom/{customTodoId}", customTodo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("커스텀 숙제 체크")
    @MeasurePerformance(maxQueries = 15)
    void check() throws Exception {
        CustomTodo customTodo = createTestCustomTodo();

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("customTodoId", customTodo.getId());

        mockMvc.perform(post("/api/v1/custom/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("커스텀 숙제 삭제")
    @MeasurePerformance(maxQueries = 15)
    void remove() throws Exception {
        CustomTodo customTodo = createTestCustomTodo();

        mockMvc.perform(delete("/api/v1/custom/{customTodoId}", customTodo.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private CustomTodo createTestCustomTodo() {
        return customTodoRepository.save(CustomTodo.builder()
                .character(testCharacter)
                .contentName("테스트 커스텀 숙제")
                .frequency(CustomTodoFrequencyEnum.DAILY)
                .isChecked(false)
                .build());
    }
}
