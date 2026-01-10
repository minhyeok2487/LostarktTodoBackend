package lostark.todo.domain.notification.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.enums.NotificationType;
import lostark.todo.domain.notification.repository.NotificationRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class NotificationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;
    private Member testMember;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
        testMember = memberService.get(TEST_USERNAME);
    }

    @Test
    @DisplayName("알림 조회")
    @MeasurePerformance(maxQueries = 15)
    void search() throws Exception {
        mockMvc.perform(get("/api/v1/notification")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 상태 조회")
    @MeasurePerformance(maxQueries = 5)
    void getStatus() throws Exception {
        mockMvc.perform(get("/api/v1/notification/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 일괄 확인")
    @MeasurePerformance(maxQueries = 10)
    void updateReadAll() throws Exception {
        mockMvc.perform(post("/api/v1/notification/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 확인")
    @MeasurePerformance(maxQueries = 10)
    void updateRead() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .content("테스트 알림")
                .isRead(false)
                .notificationType(NotificationType.BOARD)
                .boardId(1L)
                .receiver(testMember)
                .build());

        mockMvc.perform(post("/api/v1/notification/{notificationId}", notification.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
