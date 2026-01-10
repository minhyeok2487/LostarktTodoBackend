package lostark.todo.domain.admin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.enums.Role;
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

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class AdminApiTest {

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
    private boolean isAdmin;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
        isAdmin = testMember.getRole() == Role.ADMIN;
    }

    @Test
    @DisplayName("어드민 회원 정보 조회")
    @MeasurePerformance(maxQueries = 10)
    void getMember() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/member")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 가입자 수 통계 조회")
    @MeasurePerformance(maxQueries = 10)
    void searchMemberDashBoard() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/dash-board/member")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 가입 캐릭터 수 통계 조회")
    @MeasurePerformance(maxQueries = 10)
    void searchCharactersDashBoard() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/dash-board/characters")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 회원 목록 조회")
    @MeasurePerformance(maxQueries = 15)
    void searchMembers() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 회원 상세 조회")
    @MeasurePerformance(maxQueries = 10)
    void getMemberDetail() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/members/" + testMember.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("후원 목록 조회")
    @MeasurePerformance(maxQueries = 10)
    void searchAds() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/ads")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("광고 제거 날짜 변경")
    @MeasurePerformance(maxQueries = 10)
    void updateAdsDate() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        Map<String, Object> request = new HashMap<>();
        request.put("proposerEmail", TEST_USERNAME);
        request.put("price", 10000);

        mockMvc.perform(post("/admin/api/v1/ads/date")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("컨텐츠 추가")
    @MeasurePerformance(maxQueries = 15)
    void addContent() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        Map<String, Object> request = new HashMap<>();
        request.put("contentType", "day");
        request.put("name", "테스트 카오스던전");
        request.put("level", 1700.0);
        request.put("category", "카오스던전");
        request.put("shilling", 100000.0);
        request.put("honorShard", 1000.0);
        request.put("leapStone", 10.0);
        request.put("destructionStone", 500.0);
        request.put("guardianStone", 1000.0);
        request.put("jewelry", 0.0);

        mockMvc.perform(post("/admin/api/v1/content")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
