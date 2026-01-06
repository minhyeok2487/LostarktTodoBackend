package lostark.todo.domain.generaltodo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.repository.GeneralTodoCategoryRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoFolderRepository;
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
class GeneralTodoCategoryApiTest {

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

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;
    private GeneralTodoFolder testFolder;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
        testFolder = generalTodoFolderRepository.save(GeneralTodoFolder.builder()
                .member(testMember)
                .name("테스트 폴더")
                .sortOrder(0)
                .build());
    }

    @Test
    @DisplayName("카테고리 생성")
    @MeasurePerformance(maxQueries = 10)
    void createCategory() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "테스트 카테고리");
        request.put("color", "#FF0000");

        mockMvc.perform(post("/api/v1/general-todos/categories/folders/{folderId}", testFolder.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("카테고리 수정")
    @MeasurePerformance(maxQueries = 10)
    void updateCategory() throws Exception {
        GeneralTodoCategory category = createTestCategory();

        Map<String, Object> request = new HashMap<>();
        request.put("name", "수정된 카테고리");
        request.put("color", "#00FF00");

        mockMvc.perform(patch("/api/v1/general-todos/categories/{categoryId}", category.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 순서 변경")
    @MeasurePerformance(maxQueries = 15)
    void reorderCategories() throws Exception {
        GeneralTodoCategory category1 = createTestCategory();
        GeneralTodoCategory category2 = createTestCategory();

        List<Long> categoryIds = generalTodoCategoryRepository.findIdsByFolder(testFolder.getId(), testMember.getId());
        List<Long> reorderedIds = new java.util.ArrayList<>(categoryIds);
        java.util.Collections.reverse(reorderedIds);

        Map<String, Object> request = new HashMap<>();
        request.put("categoryIds", reorderedIds);

        mockMvc.perform(patch("/api/v1/general-todos/categories/folders/{folderId}/reorder", testFolder.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("카테고리 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteCategory() throws Exception {
        GeneralTodoCategory category = createTestCategory();

        mockMvc.perform(delete("/api/v1/general-todos/categories/{categoryId}", category.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private GeneralTodoCategory createTestCategory() {
        return generalTodoCategoryRepository.save(GeneralTodoCategory.builder()
                .folder(testFolder)
                .member(testMember)
                .name("테스트 카테고리")
                .color("#FF0000")
                .sortOrder(0)
                .build());
    }
}
