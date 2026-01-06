package lostark.todo.domain.generaltodo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.repository.GeneralTodoCategoryRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoFolderRepository;
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
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class GeneralTodoStatusApiTest {

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

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;
    private GeneralTodoFolder testFolder;
    private GeneralTodoCategory testCategory;

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
    }

    @Test
    @DisplayName("상태 생성")
    @MeasurePerformance(maxQueries = 10)
    void createStatus() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "진행중");

        mockMvc.perform(post("/api/v1/general-todos/categories/{categoryId}/statuses", testCategory.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상태 이름 변경")
    @MeasurePerformance(maxQueries = 10)
    void renameStatus() throws Exception {
        GeneralTodoStatus status = createTestStatus(0);

        Map<String, Object> request = new HashMap<>();
        request.put("name", "완료됨");

        mockMvc.perform(patch("/api/v1/general-todos/categories/{categoryId}/statuses/{statusId}",
                        testCategory.getId(), status.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상태 순서 변경")
    @MeasurePerformance(maxQueries = 15)
    void reorderStatuses() throws Exception {
        GeneralTodoStatus status1 = createTestStatus(0);
        GeneralTodoStatus status2 = createTestStatus(1);

        List<Long> statusIds = generalTodoStatusRepository.findIdsByCategory(testCategory.getId(), testMember.getId());
        List<Long> reorderedIds = new java.util.ArrayList<>(statusIds);
        java.util.Collections.reverse(reorderedIds);

        Map<String, Object> request = new HashMap<>();
        request.put("statusIds", reorderedIds);

        mockMvc.perform(patch("/api/v1/general-todos/categories/{categoryId}/statuses/reorder", testCategory.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("상태 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteStatus() throws Exception {
        // 삭제를 위해 최소 2개 이상의 상태 필요
        createTestStatus(0);
        GeneralTodoStatus statusToDelete = createTestStatus(1);

        mockMvc.perform(delete("/api/v1/general-todos/categories/{categoryId}/statuses/{statusId}",
                        testCategory.getId(), statusToDelete.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private GeneralTodoStatus createTestStatus(int sortOrder) {
        return generalTodoStatusRepository.save(GeneralTodoStatus.builder()
                .category(testCategory)
                .member(testMember)
                .name("테스트 상태 " + sortOrder)
                .sortOrder(sortOrder)
                .build());
    }
}
