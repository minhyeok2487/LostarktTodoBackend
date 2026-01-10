package lostark.todo.domain.servertodo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.entity.ServerTodoState;
import lostark.todo.domain.servertodo.repository.ServerTodoRepository;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class ServerTodoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ServerTodoRepository serverTodoRepository;

    @Autowired
    private ServerTodoStateRepository serverTodoStateRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";
    private static final String TEST_SERVER_NAME = "루페온";

    private String token;
    private Member testMember;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
    }

    @Test
    @DisplayName("서버 공통 숙제 조회")
    @MeasurePerformance(maxQueries = 10)
    void getServerTodos() throws Exception {
        mockMvc.perform(get("/api/v1/server-todos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("서버 공통 숙제 생성")
    @MeasurePerformance(maxQueries = 10)
    void createServerTodo() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("contentName", "테스트 서버 숙제");
        request.put("defaultEnabled", true);

        mockMvc.perform(post("/api/v1/server-todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("서버 공통 숙제 on/off")
    @MeasurePerformance(maxQueries = 15)
    void toggleEnabled() throws Exception {
        ServerTodo serverTodo = createTestServerTodoWithState();

        Map<String, Object> request = new HashMap<>();
        request.put("serverName", TEST_SERVER_NAME);
        request.put("enabled", false);

        mockMvc.perform(patch("/api/v1/server-todos/{todoId}/toggle-enabled", serverTodo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("서버 공통 숙제 체크 여부 변경")
    @MeasurePerformance(maxQueries = 15)
    void check() throws Exception {
        ServerTodo serverTodo = createTestServerTodoWithState();

        Map<String, Object> request = new HashMap<>();
        request.put("serverName", TEST_SERVER_NAME);
        request.put("checked", true);

        mockMvc.perform(post("/api/v1/server-todos/{todoId}/check", serverTodo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private ServerTodo createTestServerTodoWithState() {
        ServerTodo serverTodo = serverTodoRepository.save(ServerTodo.builder()
                .contentName("테스트 숙제")
                .defaultEnabled(true)
                .build());

        serverTodoStateRepository.save(ServerTodoState.create(serverTodo, testMember, TEST_SERVER_NAME, true));

        return serverTodo;
    }
}
