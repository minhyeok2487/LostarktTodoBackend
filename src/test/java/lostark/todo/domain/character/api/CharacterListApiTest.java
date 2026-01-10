package lostark.todo.domain.character.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.dto.CharacterSortRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class CharacterListApiTest {

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
    private Member member;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        member = memberService.get(TEST_USERNAME);
    }

    @Test
    @DisplayName("캐릭터 리스트 조회")
    @MeasurePerformance(maxQueries = 30)
    void get_characterList() throws Exception {
        // TODO: 다수 쿼리 발생 - N+1 최적화 필요
        mockMvc.perform(get("/api/v1/character-list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("삭제된 캐릭터 리스트 조회")
    @MeasurePerformance(maxQueries = 5)
    void getDeletedCharacter() throws Exception {
        mockMvc.perform(get("/api/v1/character-list/deleted")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("캐릭터 리스트 정렬 순서 변경")
    @MeasurePerformance(maxQueries = 30)
    void updateSort() throws Exception {
        List<CharacterSortRequest> sortRequests = new ArrayList<>();
        AtomicInteger sortNumber = new AtomicInteger(1);

        member.getCharacters().stream()
                .filter(c -> !c.isDeleted())
                .forEach(c -> sortRequests.add(
                        CharacterSortRequest.builder()
                                .characterName(c.getCharacterName())
                                .sortNumber(sortNumber.getAndIncrement())
                                .build()
                ));

        mockMvc.perform(patch("/api/v1/character-list/sorting")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sortRequests)))
                .andExpect(status().isOk());
    }
}
