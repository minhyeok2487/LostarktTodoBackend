package lostark.todo.domain.admin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.character.entity.Character;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        mockMvc.perform(get("/admin/api/v1/dashboard/member")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 가입자 수 통계 조회")
    @MeasurePerformance(maxQueries = 10)
    void searchMemberDashBoard() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/dashboard/daily-members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일일 가입 캐릭터 수 통계 조회")
    @MeasurePerformance(maxQueries = 10)
    void searchCharactersDashBoard() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/dashboard/daily-characters")
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
    @DisplayName("어드민 회원 정보 수정")
    @MeasurePerformance(maxQueries = 10)
    void updateMember() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        Map<String, Object> request = new HashMap<>();
        request.put("role", "USER");

        mockMvc.perform(put("/admin/api/v1/members/" + testMember.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 회원 삭제")
    @MeasurePerformance(maxQueries = 100)
    void deleteMember() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(delete("/admin/api/v1/members/" + testMember.getId())
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

        mockMvc.perform(post("/admin/api/v1/contents")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // =============== Character Admin API Tests ===============

    @Test
    @DisplayName("어드민 캐릭터 목록 조회")
    @MeasurePerformance(maxQueries = 15)
    void searchCharacters() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/characters")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 캐릭터 목록 조회 - 검색조건")
    @MeasurePerformance(maxQueries = 15)
    void searchCharactersWithFilter() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/characters")
                        .header("Authorization", "Bearer " + token)
                        .param("memberId", String.valueOf(testMember.getId()))
                        .param("minItemLevel", "1600"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 캐릭터 상세 조회")
    @MeasurePerformance(maxQueries = 10)
    void getCharacterDetail() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");
        assumeTrue(!testMember.getCharacters().isEmpty(), "테스트 캐릭터가 필요합니다");

        Character character = testMember.getCharacters().get(0);
        mockMvc.perform(get("/admin/api/v1/characters/" + character.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 캐릭터 정보 수정")
    @MeasurePerformance(maxQueries = 15)
    void updateCharacter() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");
        assumeTrue(!testMember.getCharacters().isEmpty(), "테스트 캐릭터가 필요합니다");

        Character character = testMember.getCharacters().get(0);

        Map<String, Object> request = new HashMap<>();
        request.put("memo", "테스트 메모");
        request.put("sortNumber", 1);

        mockMvc.perform(put("/admin/api/v1/characters/" + character.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 캐릭터 삭제")
    @MeasurePerformance(maxQueries = 100)
    void deleteCharacter() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");
        assumeTrue(!testMember.getCharacters().isEmpty(), "테스트 캐릭터가 필요합니다");

        Character character = testMember.getCharacters().get(0);
        mockMvc.perform(delete("/admin/api/v1/characters/" + character.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // =============== Content Admin API Tests ===============

    @Test
    @DisplayName("어드민 컨텐츠 목록 조회")
    @MeasurePerformance(maxQueries = 10)
    void getContentList() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/contents")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 컨텐츠 목록 조회 - 타입별")
    @MeasurePerformance(maxQueries = 10)
    void getContentListByType() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/contents")
                        .header("Authorization", "Bearer " + token)
                        .param("contentType", "week"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 컨텐츠 상세 조회")
    @MeasurePerformance(maxQueries = 10)
    void getContentDetail() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/contents/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 컨텐츠 수정")
    @MeasurePerformance(maxQueries = 15)
    void updateContent() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        // contentId 1은 DayContent (카오스던전)
        Map<String, Object> request = new HashMap<>();
        request.put("contentType", "day");
        request.put("name", "수정된 카오스던전");
        request.put("level", 1700.0);
        request.put("category", "카오스던전");
        request.put("shilling", 100000.0);
        request.put("honorShard", 1000.0);
        request.put("leapStone", 10.0);
        request.put("destructionStone", 500.0);
        request.put("guardianStone", 1000.0);
        request.put("jewelry", 0.0);

        mockMvc.perform(put("/admin/api/v1/contents/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // =============== Dashboard API Tests ===============

    @Test
    @DisplayName("대시보드 통계 요약 조회")
    @MeasurePerformance(maxQueries = 10)
    void getSummary() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/dashboard/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("최근 활동 조회")
    @MeasurePerformance(maxQueries = 5)
    void getRecentActivities() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/dashboard/recent-activities")
                        .header("Authorization", "Bearer " + token)
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    // =============== Comments Admin API Tests ===============

    @Test
    @DisplayName("어드민 댓글 목록 조회")
    @MeasurePerformance(maxQueries = 30)
    void getCommentList() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // =============== Friends Admin API Tests ===============

    @Test
    @DisplayName("어드민 깐부 목록 조회")
    @MeasurePerformance(maxQueries = 30)
    void getFriendList() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/friends")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // =============== Notification Admin API Tests ===============

    @Test
    @DisplayName("어드민 알림 목록 조회")
    @MeasurePerformance(maxQueries = 30)
    void getNotificationList() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        mockMvc.perform(get("/admin/api/v1/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("어드민 전체 공지 발송")
    void broadcast() throws Exception {
        assumeTrue(isAdmin, "Admin 권한이 필요합니다");

        Map<String, Object> request = new HashMap<>();
        request.put("content", "테스트 공지사항입니다.");

        mockMvc.perform(post("/admin/api/v1/notifications/broadcast")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
