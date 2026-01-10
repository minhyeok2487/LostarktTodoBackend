package lostark.todo.domain.friend.api;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class FriendApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

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
    @DisplayName("깐부 리스트 조회")
    @MeasurePerformance(maxQueries = 100)
    void getFriendList() throws Exception {
        // TODO: 심각한 N+1 문제 - 최적화 필수
        mockMvc.perform(get("/api/v1/friend")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("캐릭터 검색")
    @MeasurePerformance(maxQueries = 10)
    void findCharacter() throws Exception {
        mockMvc.perform(get("/api/v1/friend/character/{characterName}", testCharacter.getCharacterName())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
