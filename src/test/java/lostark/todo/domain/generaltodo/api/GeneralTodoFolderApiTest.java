package lostark.todo.domain.generaltodo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
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
class GeneralTodoFolderApiTest {

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

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
    }

    @Test
    @DisplayName("폴더 생성")
    @MeasurePerformance(maxQueries = 10)
    void createFolder() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "테스트 폴더");

        mockMvc.perform(post("/api/v1/general-todos/folders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("폴더 이름 변경")
    @MeasurePerformance(maxQueries = 10)
    void renameFolder() throws Exception {
        GeneralTodoFolder folder = createTestFolder();

        Map<String, Object> request = new HashMap<>();
        request.put("name", "변경된 폴더명");

        mockMvc.perform(patch("/api/v1/general-todos/folders/{folderId}", folder.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("폴더 순서 변경")
    @MeasurePerformance(maxQueries = 15)
    void reorderFolders() throws Exception {
        GeneralTodoFolder folder1 = createTestFolder();
        GeneralTodoFolder folder2 = createTestFolder();

        // 기존 폴더 ID + 새로 생성한 폴더 ID를 역순으로 정렬
        List<Long> existingFolderIds = generalTodoFolderRepository.findIdsByMemberId(testMember.getId());
        List<Long> reorderedIds = new java.util.ArrayList<>(existingFolderIds);
        java.util.Collections.reverse(reorderedIds);

        Map<String, Object> request = new HashMap<>();
        request.put("folderIds", reorderedIds);

        mockMvc.perform(patch("/api/v1/general-todos/folders/reorder")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("폴더 삭제")
    @MeasurePerformance(maxQueries = 10)
    void deleteFolder() throws Exception {
        GeneralTodoFolder folder = createTestFolder();

        mockMvc.perform(delete("/api/v1/general-todos/folders/{folderId}", folder.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private GeneralTodoFolder createTestFolder() {
        return generalTodoFolderRepository.save(GeneralTodoFolder.builder()
                .member(testMember)
                .name("테스트 폴더")
                .sortOrder(0)
                .build());
    }
}
