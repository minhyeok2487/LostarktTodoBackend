package lostark.todo.domain.generaltodo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.repository.GeneralTodoCategoryRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoFolderRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoItemRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoStatusRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class GeneralTodoItemApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GeneralTodoFolderRepository generalTodoFolderRepository;

    @Autowired
    private GeneralTodoCategoryRepository generalTodoCategoryRepository;

    @Autowired
    private GeneralTodoStatusRepository generalTodoStatusRepository;

    @Autowired
    private GeneralTodoItemRepository generalTodoItemRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;
    private GeneralTodoFolder testFolder;
    private GeneralTodoCategory testCategory;
    private GeneralTodoStatus testStatus;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
        testFolder = generalTodoFolderRepository.save(GeneralTodoFolder.builder()
                .member(testMember)
                .name("테스트 폴더")
                .sortOrder(0)
                .build());
        testCategory = generalTodoCategoryRepository.save(GeneralTodoCategory.builder()
                .folder(testFolder)
                .member(testMember)
                .name("테스트 카테고리")
                .sortOrder(0)
                .build());
        testStatus = generalTodoStatusRepository.save(GeneralTodoStatus.builder()
                .category(testCategory)
                .member(testMember)
                .name("대기")
                .sortOrder(0)
                .build());
    }

    @Test
    @DisplayName("할 일 생성")
    @MeasurePerformance(maxQueries = 15)
    void createItem() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("folderId", testFolder.getId());
        request.put("categoryId", testCategory.getId());
        request.put("statusId", testStatus.getId());
        request.put("title", "테스트 할 일");

        mockMvc.perform(post("/api/v1/general-todos/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("할 일 수정")
    @MeasurePerformance(maxQueries = 15)
    void updateItem() throws Exception {
        GeneralTodoItem item = createTestItem();

        Map<String, Object> request = new HashMap<>();
        request.put("title", "수정된 할 일");
        request.put("description", "상세 설명 추가");

        mockMvc.perform(patch("/api/v1/general-todos/items/{itemId}", item.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("할 일 상태 변경")
    @MeasurePerformance(maxQueries = 15)
    void updateStatus() throws Exception {
        GeneralTodoItem item = createTestItem();
        GeneralTodoStatus newStatus = generalTodoStatusRepository.save(GeneralTodoStatus.builder()
                .category(testCategory)
                .member(testMember)
                .name("완료")
                .sortOrder(1)
                .build());

        Map<String, Object> request = new HashMap<>();
        request.put("statusId", newStatus.getId());

        mockMvc.perform(patch("/api/v1/general-todos/items/{itemId}/status", item.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("할 일 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteItem() throws Exception {
        GeneralTodoItem item = createTestItem();

        mockMvc.perform(delete("/api/v1/general-todos/items/{itemId}", item.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private GeneralTodoItem createTestItem() {
        return generalTodoItemRepository.save(GeneralTodoItem.builder()
                .folder(testFolder)
                .category(testCategory)
                .member(testMember)
                .status(testStatus)
                .title("테스트 할 일")
                .allDay(false)
                .build());
    }
}
