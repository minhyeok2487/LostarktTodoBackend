package lostark.todo.domain.character.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.repository.ContentRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CharacterWeekApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoV2Repository todoV2Repository;

    @Autowired
    private ContentRepository contentRepository;

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
    @DisplayName("주간 레이드 추가 폼 조회")
    @MeasurePerformance(maxQueries = 10)
    void getTodoForm() throws Exception {
        mockMvc.perform(get("/api/v1/character/week/raid/form")
                        .header("Authorization", "Bearer " + token)
                        .param("characterId", String.valueOf(testCharacter.getId())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 에포나 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateWeekEpona() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/character/week/epona")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 실마엘 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateWeekSilmael() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/character/week/silmael")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 큐브 티켓 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateWeekCubeTicket() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/character/week/cube")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("골드 체크 방식 업데이트")
    @MeasurePerformance(maxQueries = 10)
    void updateGoldCheckVersion() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(patch("/api/v1/character/week/gold-check-version")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("낙원(천상) 업데이트")
    @MeasurePerformance(maxQueries = 10)
    void updateElysian() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("action", "INCREMENT");

        mockMvc.perform(post("/api/v1/character/week/elysian")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("낙원(천상) 전체 체크")
    @MeasurePerformance(maxQueries = 10)
    void updateElysianAll() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(post("/api/v1/character/week/elysian/all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지옥 열쇠 증가")
    @MeasurePerformance(maxQueries = 10)
    void updateHellKey_increment() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("num", 1);

        mockMvc.perform(post("/api/v1/character/week/hell-key")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지옥 열쇠 감소")
    @MeasurePerformance(maxQueries = 10)
    void updateHellKey_decrement() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("num", -1);

        mockMvc.perform(post("/api/v1/character/week/hell-key")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 추가/제거")
    @MeasurePerformance(maxQueries = 20)
    void updateWeekRaid() throws Exception {
        List<WeekContent> weekContents = contentRepository.findAllWeekContent(testCharacter.getItemLevel());
        if (weekContents.isEmpty()) {
            return; // 캐릭터 레벨에 맞는 레이드가 없으면 스킵
        }

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("weekContentIdList", List.of(weekContents.get(0).getId()));

        mockMvc.perform(post("/api/v1/character/week/raid")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 버스 골드 수정")
    @MeasurePerformance(maxQueries = 15)
    void updateWeekRaidBusGold() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        String weekCategory = todoV2.getWeekContent().getWeekCategory();
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("weekCategory", weekCategory);
        request.put("busGold", 1000);
        request.put("fixed", false);

        mockMvc.perform(post("/api/v1/character/week/raid/bus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 순서 변경")
    @MeasurePerformance(maxQueries = 15)
    void updateWeekRaidSort() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        String weekCategory = todoV2.getWeekContent().getWeekCategory();
        Map<String, Object> sortRequest = new HashMap<>();
        sortRequest.put("weekCategory", weekCategory);
        sortRequest.put("sortNumber", 1);

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("sortRequestList", List.of(sortRequest));

        mockMvc.perform(post("/api/v1/character/week/raid/sort")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 체크")
    @MeasurePerformance(maxQueries = 20)
    void updateWeekRaidCheck() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        String weekCategory = todoV2.getWeekContent().getWeekCategory();
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("weekCategory", weekCategory);
        request.put("allCheck", false);

        mockMvc.perform(post("/api/v1/character/week/raid/check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 메모 수정")
    @MeasurePerformance(maxQueries = 15)
    void updateWeekRaidMessage() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("todoId", todoV2.getId());
        request.put("message", "테스트 메모");

        mockMvc.perform(post("/api/v1/character/week/raid/message")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 골드 체크")
    @MeasurePerformance(maxQueries = 15)
    void updateRaidGoldCheck() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        String weekCategory = todoV2.getWeekContent().getWeekCategory();
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("weekCategory", weekCategory);
        request.put("updateValue", true);

        mockMvc.perform(patch("/api/v1/character/week/raid/gold-check")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 레이드 더보기 체크")
    @MeasurePerformance(maxQueries = 15)
    void updateRaidMoreRewardCheck() throws Exception {
        TodoV2 todoV2 = getOrCreateTodoV2();
        if (todoV2 == null) {
            return; // TodoV2 데이터가 없으면 스킵
        }

        String weekCategory = todoV2.getWeekContent().getWeekCategory();
        int gate = todoV2.getWeekContent().getGate();
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("weekCategory", weekCategory);
        request.put("gate", gate);

        mockMvc.perform(post("/api/v1/character/week/raid/more-reward")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private TodoV2 getOrCreateTodoV2() {
        List<TodoV2> existingTodos = testCharacter.getTodoV2List();
        if (existingTodos != null && !existingTodos.isEmpty()) {
            return existingTodos.get(0);
        }

        List<WeekContent> weekContents = contentRepository.findAllWeekContent(testCharacter.getItemLevel());
        if (weekContents.isEmpty()) {
            return null;
        }

        TodoV2 todoV2 = TodoV2.builder()
                .character(testCharacter)
                .weekContent(weekContents.get(0))
                .isChecked(false)
                .gold(weekContents.get(0).getGold())
                .coolTime(2)
                .build();
        return todoV2Repository.save(todoV2);
    }
}
