package lostark.todo.domain.inspection.service;

import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.CombatPowerHistory;
import lostark.todo.domain.inspection.entity.InspectionCharacter;
import lostark.todo.domain.inspection.repository.CombatPowerHistoryRepository;
import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.service.NotificationService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class InspectionServiceTest {

    @Mock
    private InspectionCharacterRepository inspectionCharacterRepository;

    @Mock
    private CombatPowerHistoryRepository combatPowerHistoryRepository;

    @Mock
    private LostarkCharacterApiClient lostarkCharacterApiClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private InspectionService inspectionService;

    private Member testMember;
    private InspectionCharacter testCharacter;
    private CharacterJsonDto testProfile;
    private CombatPowerHistory testHistory;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .apiKey("test-api-key")
                .build();

        testCharacter = InspectionCharacter.builder()
                .id(1L)
                .member(testMember)
                .characterName("테스트캐릭터")
                .serverName("루페온")
                .characterClassName("버서커")
                .characterImage("https://img.test.com/char.png")
                .itemLevel(1620.0)
                .combatPower(2200.0)
                .noChangeThreshold(3)
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        testProfile = new CharacterJsonDto();
        testProfile.setCharacterName("테스트캐릭터");
        testProfile.setServerName("루페온");
        testProfile.setCharacterClassName("버서커");
        testProfile.setCharacterImage("https://img.test.com/char.png");
        testProfile.setItemAvgLevel(1620.0);
        testProfile.setCombatPower(2250.0);

        testHistory = CombatPowerHistory.builder()
                .id(1L)
                .inspectionCharacter(testCharacter)
                .recordDate(LocalDate.now())
                .combatPower(2200.0)
                .itemLevel(1620.0)
                .arkgridEffects(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("성공 - 캐릭터 등록")
        void success() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(Collections.emptyList());
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(Collections.emptyList());

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("테스트캐릭터");
            request.setNoChangeThreshold(3);

            // when
            InspectionCharacterResponse response = inspectionService.create("test@test.com", request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getCharacterName()).isEqualTo("테스트캐릭터");
            assertThat(response.getServerName()).isEqualTo("루페온");
            assertThat(response.getCombatPower()).isEqualTo(2250.0);
            verify(inspectionCharacterRepository).save(any(InspectionCharacter.class));
        }

        @Test
        @DisplayName("실패 - API 키 없음")
        void fail_noApiKey() {
            // given
            Member noKeyMember = Member.builder()
                    .id(2L)
                    .username("nokey@test.com")
                    .build();
            given(memberService.get("nokey@test.com")).willReturn(noKeyMember);

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("테스트캐릭터");

            // when & then
            assertThatThrownBy(() -> inspectionService.create("nokey@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("API 키가 등록되어 있지 않습니다");
        }

        @Test
        @DisplayName("실패 - 이미 등록된 캐릭터")
        void fail_duplicateCharacter() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(testCharacter));

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("테스트캐릭터");

            // when & then
            assertThatThrownBy(() -> inspectionService.create("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("이미 등록된 캐릭터입니다");
        }
    }

    @Nested
    @DisplayName("getAll 메서드")
    class GetAllTest {

        @Test
        @DisplayName("성공 - 캐릭터 목록 조회")
        void success() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(testCharacter));
            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Collections.emptyMap());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Collections.emptyMap());

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCharacterName()).isEqualTo("테스트캐릭터");
        }

        @Test
        @DisplayName("성공 - 빈 목록")
        void success_emptyList() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(Collections.emptyList());

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공 - 변화량 정보가 포함된 목록 (히스토리 2개 이상)")
        void success_withChangeInfo() {
            // given
            CombatPowerHistory history1 = CombatPowerHistory.builder()
                    .inspectionCharacter(testCharacter)
                    .combatPower(2200.0)
                    .recordDate(LocalDate.now().minusDays(1))
                    .arkgridEffects(new ArrayList<>())
                    .build();
            CombatPowerHistory history2 = CombatPowerHistory.builder()
                    .inspectionCharacter(testCharacter)
                    .combatPower(2250.0)
                    .recordDate(LocalDate.now())
                    .arkgridEffects(new ArrayList<>())
                    .build();

            // findLatest2ByCharacterIds returns desc order (latest first)
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(testCharacter));
            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Map.of(1L, List.of(history2, history1)));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Map.of(1L, 0L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPreviousCombatPower()).isEqualTo(2200.0);
            assertThat(result.get(0).getCombatPowerChange()).isEqualTo(50.0);
        }
    }

    @Nested
    @DisplayName("getDetail 메서드")
    class GetDetailTest {

        @Test
        @DisplayName("성공 - 상세 조회")
        void success() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(testHistory));
            given(combatPowerHistoryRepository.findLatest2(1L)).willReturn(List.of(testHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(0L);

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCharacter().getCharacterName()).isEqualTo("테스트캐릭터");
            assertThat(result.getHistories()).hasSize(1);
        }

        @Test
        @DisplayName("실패 - 캐릭터 없음")
        void fail_notFound() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(999L, "test@test.com"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> inspectionService.getDetail(
                    "test@test.com", 999L, LocalDate.now().minusDays(30), LocalDate.now()))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("캐릭터를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("성공 - 설정 수정")
        void success() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));

            UpdateInspectionCharacterRequest request = new UpdateInspectionCharacterRequest();
            request.setNoChangeThreshold(5);
            request.setActive(false);

            // when
            inspectionService.update("test@test.com", 1L, request);

            // then
            assertThat(testCharacter.getNoChangeThreshold()).isEqualTo(5);
            assertThat(testCharacter.isActive()).isFalse();
        }

        @Test
        @DisplayName("실패 - 캐릭터 없음")
        void fail_notFound() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(999L, "test@test.com"))
                    .willReturn(Optional.empty());

            UpdateInspectionCharacterRequest request = new UpdateInspectionCharacterRequest();
            request.setNoChangeThreshold(5);

            // when & then
            assertThatThrownBy(() -> inspectionService.update("test@test.com", 999L, request))
                    .isInstanceOf(ConditionNotMetException.class);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("성공 - 캐릭터 삭제")
        void success() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));

            // when
            inspectionService.delete("test@test.com", 1L);

            // then
            verify(inspectionCharacterRepository).delete(testCharacter);
        }

        @Test
        @DisplayName("실패 - 캐릭터 없음")
        void fail_notFound() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(999L, "test@test.com"))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> inspectionService.delete("test@test.com", 999L))
                    .isInstanceOf(ConditionNotMetException.class);
        }
    }

    @Nested
    @DisplayName("refresh 메서드")
    class RefreshTest {

        @Test
        @DisplayName("성공 - 수동 새로고침")
        void success() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(testHistory));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(anyLong(), any(), any()))
                    .willReturn(List.of(testHistory));
            given(combatPowerHistoryRepository.findLatest2(anyLong())).willReturn(List.of(testHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            InspectionDashboardResponse result = inspectionService.refresh("test@test.com", 1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCharacter()).isNotNull();
        }

        @Test
        @DisplayName("실패 - API 키 없음")
        void fail_noApiKey() {
            // given
            Member noKeyMember = Member.builder()
                    .id(2L)
                    .username("nokey@test.com")
                    .build();
            given(memberService.get("nokey@test.com")).willReturn(noKeyMember);

            // when & then
            assertThatThrownBy(() -> inspectionService.refresh("nokey@test.com", 1L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("API 키가 등록되어 있지 않습니다");
        }
    }

    @Nested
    @DisplayName("fetchDailyData 메서드")
    class FetchDailyDataTest {

        @Test
        @DisplayName("성공 - 데이터 수집 및 전투력 증가 알림")
        void success_withIncreaseNotification() {
            // given
            testCharacter.setCombatPower(2200.0);
            testProfile.setCombatPower(2300.0);

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(List.of(new ArkgridEffectDto("공격력 증가", 5, "공격력이 증가합니다.")));
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            assertThat(testCharacter.getCombatPower()).isEqualTo(2300.0);
            verify(combatPowerHistoryRepository).save(any(CombatPowerHistory.class));
            // 전투력 증가 알림
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("전투력이 증가했습니다"), eq(1L));
        }

        @Test
        @DisplayName("성공 - N일 무변동 알림")
        void success_withNoChangeNotification() {
            // given
            testCharacter.setCombatPower(2200.0);
            testProfile.setCombatPower(2200.0);  // 변동 없음

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(testHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(5L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            // 무변동 알림 (threshold=3, unchangedDays=5)
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("변동이 없습니다"), eq(1L));
        }

        @Test
        @DisplayName("성공 - 기존 히스토리 업데이트 (upsert)")
        void success_updateExistingHistory() {
            // given
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(testHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            // 기존 히스토리가 있으면 save가 호출되지 않음 (updateData로 업데이트)
            verify(combatPowerHistoryRepository, never()).save(any(CombatPowerHistory.class));
            assertThat(testHistory.getCombatPower()).isEqualTo(2250.0);
        }

        @Test
        @DisplayName("성공 - API 호출 실패 시 예외가 전파되지 않음")
        void success_apiFailureDoesNotPropagate() {
            // given
            given(lostarkCharacterApiClient.getCharacterProfileForInspection(anyString(), anyString()))
                    .willThrow(new RuntimeException("API 호출 실패"));

            // when - 예외가 전파되지 않아야 함
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(notificationService, never()).createInspectionNotification(any(), anyString(), anyLong());
        }
    }
}
