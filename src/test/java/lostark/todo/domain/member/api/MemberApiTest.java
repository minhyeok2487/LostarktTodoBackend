package lostark.todo.domain.member.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class MemberApiTest {

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
    @DisplayName("회원 정보 조회")
    @MeasurePerformance(maxQueries = 10)
    void getMember() throws Exception {
        mockMvc.perform(get("/api/v1/member")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대표 캐릭터 변경")
    @MeasurePerformance(maxQueries = 10)
    void editMainCharacter() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("mainCharacter", testCharacter.getCharacterName());

        mockMvc.perform(patch("/api/v1/member/main-character")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
