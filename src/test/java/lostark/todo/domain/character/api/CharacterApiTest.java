package lostark.todo.domain.character.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CharacterApiTest {

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
    @DisplayName("캐릭터 출력 설정 수정")
    @MeasurePerformance(maxQueries = 10)
    void updateSettings() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("characterName", testCharacter.getCharacterName());
        request.put("name", "showEpona");
        request.put("value", true);

        mockMvc.perform(patch("/api/v1/character/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("골드 획득 캐릭터 지정/해제")
    @MeasurePerformance(maxQueries = 10)
    void updateGoldCharacter() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(patch("/api/v1/character/gold-character")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("캐릭터 메모 업데이트")
    @MeasurePerformance(maxQueries = 10)
    void updateMemo() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());
        request.put("memo", "테스트 메모");

        mockMvc.perform(post("/api/v1/character/memo")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("캐릭터 상태 변경 (삭제/복구)")
    @MeasurePerformance(maxQueries = 10)
    void updateCharacterStatus() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("characterId", testCharacter.getId());

        mockMvc.perform(patch("/api/v1/character/deleted")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
