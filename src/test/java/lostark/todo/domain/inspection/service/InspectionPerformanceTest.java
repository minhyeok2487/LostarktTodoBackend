package lostark.todo.domain.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.ArkgridEffectHistory;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 성능 개선 코드 단위 테스트
 * 7개 성능 개선 항목별 성공/실패/엣지 케이스를 검증
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class InspectionPerformanceTest {

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

    private InspectionService inspectionService;

    // InspectionScheduleService 내부의 inspectionService를 mock으로 주입하기 위해 별도 mock 사용
    @Mock
    private InspectionService inspectionServiceMock;

    // 수동으로 생성하여 inspectionServiceMock을 주입
    private InspectionScheduleService inspectionScheduleService;

    private Member testMember;
    private InspectionCharacter char1;
    private InspectionCharacter char2;
    private InspectionCharacter char3;
    private CharacterJsonDto testProfile;

    @BeforeEach
    void setUp() {
        ExecutorService realExecutor = new ThreadPoolExecutor(
                4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        InspectionPersistenceService inspectionPersistenceService = new InspectionPersistenceService(
                combatPowerHistoryRepository, notificationService, new ObjectMapper());
        inspectionService = new InspectionService(
                inspectionCharacterRepository,
                combatPowerHistoryRepository,
                lostarkCharacterApiClient,
                inspectionPersistenceService,
                memberService,
                realExecutor);

        // InspectionScheduleService에 mock InspectionService 주입
        inspectionScheduleService = new InspectionScheduleService(
                inspectionCharacterRepository, inspectionServiceMock);

        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .apiKey("test-api-key")
                .inspectionScheduleHour(7)
                .build();

        char1 = InspectionCharacter.builder()
                .id(1L)
                .member(testMember)
                .characterName("캐릭터1")
                .serverName("루페온")
                .characterClassName("버서커")
                .characterImage("https://img.test.com/char1.png")
                .itemLevel(1620.0)
                .combatPower(2200.0)
                .noChangeThreshold(3)
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        char2 = InspectionCharacter.builder()
                .id(2L)
                .member(testMember)
                .characterName("캐릭터2")
                .serverName("루페온")
                .characterClassName("소서리스")
                .characterImage("https://img.test.com/char2.png")
                .itemLevel(1600.0)
                .combatPower(2100.0)
                .noChangeThreshold(5)
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        char3 = InspectionCharacter.builder()
                .id(3L)
                .member(testMember)
                .characterName("캐릭터3")
                .serverName("실리안")
                .characterClassName("건슬링어")
                .characterImage("https://img.test.com/char3.png")
                .itemLevel(1580.0)
                .combatPower(2000.0)
                .noChangeThreshold(3)
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        testProfile = new CharacterJsonDto();
        testProfile.setCharacterName("캐릭터1");
        testProfile.setServerName("루페온");
        testProfile.setCharacterClassName("버서커");
        testProfile.setCharacterImage("https://img.test.com/char1.png");
        testProfile.setItemAvgLevel(1620.0);
        testProfile.setCombatPower(2300.0);

        // 신규 API 기본 스텁
        given(lostarkCharacterApiClient.getEquipment(anyString(), anyString()))
                .willReturn(Collections.emptyList());
        given(lostarkCharacterApiClient.getEngravings(anyString(), anyString()))
                .willReturn(Collections.emptyList());
        given(lostarkCharacterApiClient.getCards(anyString(), anyString()))
                .willReturn(new CardApiResponse(Collections.emptyList(), Collections.emptyList()));
        given(lostarkCharacterApiClient.getGems(anyString(), anyString()))
                .willReturn(Collections.emptyList());
        given(lostarkCharacterApiClient.getArkPassive(anyString(), anyString()))
                .willReturn(new ArkPassiveApiResponse(null, Collections.emptyList(), Collections.emptyList()));
    }

    // =========================================================================
    // 1. countConsecutiveUnchangedDays 최적화 테스트
    // =========================================================================
    @Nested
    @DisplayName("1. countConsecutiveUnchangedDays 최적화")
    class CountConsecutiveUnchangedDaysTest {

        @Test
        @DisplayName("성공 - 연속 3일 무변동이면 3 반환")
        void success_threeConsecutiveUnchangedDays() {
            // given
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(3L);

            // when
            long result = combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L);

            // then
            assertThat(result).isEqualTo(3L);
        }

        @Test
        @DisplayName("성공 - 매일 전투력 변화하면 1 반환")
        void success_dailyChange() {
            // given
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);

            // when
            long result = combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L);

            // then
            assertThat(result).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 히스토리 없으면 0 반환")
        void success_noHistory() {
            // given
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(0L);

            // when
            long result = combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L);

            // then
            assertThat(result).isEqualTo(0L);
        }

        @Test
        @DisplayName("엣지 - 히스토리 1개만 있으면 1 반환")
        void edge_singleHistory() {
            // given
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);

            // when
            long result = combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L);

            // then
            assertThat(result).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 무변동 일수가 threshold 이상이면 알림 발생")
        void success_notificationTriggered() {
            // given
            char1.setCombatPower(2200.0);
            testProfile.setCombatPower(2200.0); // 변동 없음

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(buildHistory(char1, 2200.0, 0)));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(5L);

            // when
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then - threshold=3, unchangedDays=5 이므로 알림 발생
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("변동이 없습니다"), eq(1L));
        }

        @Test
        @DisplayName("성공 - 무변동 일수가 threshold 미만이면 알림 미발생")
        void success_noNotificationBelowThreshold() {
            // given
            char1.setCombatPower(2200.0);
            testProfile.setCombatPower(2200.0);

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(buildHistory(char1, 2200.0, 0)));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(2L);

            // when
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then - threshold=3, unchangedDays=2 이므로 알림 미발생
            verify(notificationService, never()).createInspectionNotification(
                    any(), contains("변동이 없습니다"), anyLong());
        }
    }

    // =========================================================================
    // 2. getAll() 배치 쿼리 테스트
    // =========================================================================
    @Nested
    @DisplayName("2. getAll() 배치 쿼리")
    class GetAllBatchQueryTest {

        @Test
        @DisplayName("성공 - 여러 캐릭터 모두 변화 정보 포함")
        void success_multipleCharactersWithChangeInfo() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember))
                    .willReturn(List.of(char1, char2));

            CombatPowerHistory h1Latest = buildHistoryWithItemLevel(char1, 2250.0, 1625.0, 0);
            CombatPowerHistory h1Prev = buildHistoryWithItemLevel(char1, 2200.0, 1620.0, 1);
            CombatPowerHistory h2Latest = buildHistoryWithItemLevel(char2, 2150.0, 1610.0, 0);
            CombatPowerHistory h2Prev = buildHistoryWithItemLevel(char2, 2100.0, 1600.0, 1);

            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(List.of(1L, 2L)))
                    .willReturn(Map.of(
                            1L, List.of(h1Latest, h1Prev),
                            2L, List.of(h2Latest, h2Prev)
                    ));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(List.of(1L, 2L)))
                    .willReturn(Map.of(1L, 1L, 2L, 1L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(2);

            InspectionCharacterResponse resp1 = result.get(0);
            assertThat(resp1.getCharacterName()).isEqualTo("캐릭터1");
            assertThat(resp1.getCombatPowerChange()).isEqualTo(50.0);
            assertThat(resp1.getPreviousCombatPower()).isEqualTo(2200.0);
            assertThat(resp1.getItemLevelChange()).isEqualTo(5.0);
            assertThat(resp1.getPreviousItemLevel()).isEqualTo(1620.0);
            assertThat(resp1.getUnchangedDays()).isEqualTo(1L);

            InspectionCharacterResponse resp2 = result.get(1);
            assertThat(resp2.getCharacterName()).isEqualTo("캐릭터2");
            assertThat(resp2.getCombatPowerChange()).isEqualTo(50.0);
            assertThat(resp2.getPreviousCombatPower()).isEqualTo(2100.0);
            assertThat(resp2.getItemLevelChange()).isEqualTo(10.0);
            assertThat(resp2.getPreviousItemLevel()).isEqualTo(1600.0);
        }

        @Test
        @DisplayName("성공 - 캐릭터 0개 빈 리스트 반환")
        void success_emptyCharacterList() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(Collections.emptyList());

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).isEmpty();
            // 배치 쿼리가 호출되지 않아야 함
            verify(combatPowerHistoryRepository, never()).findLatest2ByCharacterIds(anyList());
            verify(combatPowerHistoryRepository, never()).countConsecutiveUnchangedDaysBatch(anyList());
        }

        @Test
        @DisplayName("성공 - 히스토리 없는 캐릭터는 기본값 유지")
        void success_noHistoryDefaultValues() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(char1));
            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Collections.emptyMap());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Collections.emptyMap());

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPreviousCombatPower()).isNull();
            assertThat(result.get(0).getCombatPowerChange()).isEqualTo(0.0);
            assertThat(result.get(0).getPreviousItemLevel()).isNull();
            assertThat(result.get(0).getItemLevelChange()).isEqualTo(0.0);
            assertThat(result.get(0).getUnchangedDays()).isEqualTo(0L);
        }

        @Test
        @DisplayName("성공 - 히스토리 1개만 있는 캐릭터는 변화량 계산 안됨")
        void success_singleHistoryNoChange() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(char1));

            CombatPowerHistory h1Latest = buildHistory(char1, 2250.0, 0);
            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Map.of(1L, List.of(h1Latest))); // 1개만
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Map.of(1L, 1L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPreviousCombatPower()).isNull();
            assertThat(result.get(0).getCombatPowerChange()).isEqualTo(0.0);
            assertThat(result.get(0).getPreviousItemLevel()).isNull();
            assertThat(result.get(0).getItemLevelChange()).isEqualTo(0.0);
            assertThat(result.get(0).getUnchangedDays()).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 전투력 감소도 정확히 반영")
        void success_combatPowerDecrease() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember)).willReturn(List.of(char1));

            CombatPowerHistory hLatest = buildHistory(char1, 2100.0, 0);
            CombatPowerHistory hPrev = buildHistory(char1, 2200.0, 1);
            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Map.of(1L, List.of(hLatest, hPrev)));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Map.of(1L, 1L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result.get(0).getCombatPowerChange()).isEqualTo(-100.0);
        }
    }

    // =========================================================================
    // 3. 외부 API 병렬 호출 테스트
    // =========================================================================
    @Nested
    @DisplayName("3. 외부 API 병렬 호출 (fetchDailyData)")
    class ParallelApiCallTest {

        @Test
        @DisplayName("성공 - 프로필+아크그리드 모두 정상 저장")
        void success_bothApisSucceed() {
            // given
            char1.setCombatPower(2200.0);

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            List<ArkgridEffectDto> effects = List.of(
                    new ArkgridEffectDto("공격력 증가", 5, "공격력이 증가합니다."),
                    new ArkgridEffectDto("치명 증가", 3, "치명이 증가합니다.")
            );
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(effects);
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then
            assertThat(char1.getCombatPower()).isEqualTo(2300.0);
            verify(combatPowerHistoryRepository).save(any(CombatPowerHistory.class));
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("전투력이 증가했습니다"), eq(1L));
        }

        @Test
        @DisplayName("실패 - 프로필 API 실패 시 예외 전파 안됨")
        void fail_profileApiFails() {
            // given
            given(lostarkCharacterApiClient.getCharacterProfileForInspection(anyString(), anyString()))
                    .willThrow(new RuntimeException("프로필 API 호출 실패"));
            given(lostarkCharacterApiClient.getArkgridEffects(anyString(), anyString()))
                    .willReturn(Collections.emptyList());

            // when - CompletableFuture 내부 예외는 CompletionException으로 래핑되어 catch됨
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then - 예외가 전파되지 않고 히스토리 저장 안됨
            verify(combatPowerHistoryRepository, never()).save(any(CombatPowerHistory.class));
            verify(notificationService, never()).createInspectionNotification(any(), anyString(), anyLong());
        }

        @Test
        @DisplayName("실패 - 아크그리드 API 실패 시 예외 전파 안됨")
        void fail_arkgridApiFails() {
            // given
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects(anyString(), anyString()))
                    .willThrow(new RuntimeException("아크그리드 API 호출 실패"));

            // when - CompletableFuture 내부 예외는 CompletionException으로 래핑되어 catch됨
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then - 전체 실패 (CompletableFuture.join()에서 예외 발생)
            verify(combatPowerHistoryRepository, never()).save(any(CombatPowerHistory.class));
        }

        @Test
        @DisplayName("성공 - 아크그리드 효과 빈 리스트여도 프로필은 정상 저장")
        void success_emptyArkgridEffects() {
            // given
            char1.setCombatPower(2200.0);

            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(char1, "test-api-key");

            // then
            assertThat(char1.getCombatPower()).isEqualTo(2300.0);
            verify(combatPowerHistoryRepository).save(any(CombatPowerHistory.class));
        }
    }

    // =========================================================================
    // 4. 스케줄러 트랜잭션 분리 테스트
    // =========================================================================
    @Nested
    @DisplayName("4. 스케줄러 트랜잭션 캐릭터 단위 분리")
    class SchedulerTransactionIsolationTest {

        @Test
        @DisplayName("성공 - 3개 캐릭터 중 2번째 실패해도 1,3번째 정상 처리")
        void success_partialFailure() {
            // given
            given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
                    .willReturn(List.of(char1, char2, char3));

            // char2 실패
            doNothing().when(inspectionServiceMock).fetchDailyData(char1, "test-api-key");
            doThrow(new RuntimeException("API 실패"))
                    .when(inspectionServiceMock).fetchDailyData(char2, "test-api-key");
            doNothing().when(inspectionServiceMock).fetchDailyData(char3, "test-api-key");

            // when
            inspectionScheduleService.fetchScheduledInspectionData();

            // then - 모든 캐릭터에 대해 시도됨
            verify(inspectionServiceMock).fetchDailyData(char1, "test-api-key");
            verify(inspectionServiceMock).fetchDailyData(char2, "test-api-key");
            verify(inspectionServiceMock).fetchDailyData(char3, "test-api-key");
        }

        @Test
        @DisplayName("성공 - API 키 없는 캐릭터 스킵하고 나머지 계속")
        void success_skipNoApiKeyAndContinue() {
            // given
            Member noKeyMember = Member.builder()
                    .id(2L)
                    .username("nokey@test.com")
                    .build();
            InspectionCharacter noKeyChar = InspectionCharacter.builder()
                    .id(10L)
                    .member(noKeyMember)
                    .characterName("키없는캐릭터")
                    .isActive(true)
                    .histories(new ArrayList<>())
                    .build();

            given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
                    .willReturn(List.of(char1, noKeyChar, char3));

            // when
            inspectionScheduleService.fetchScheduledInspectionData();

            // then - char1, char3은 처리되고 noKeyChar는 스킵
            verify(inspectionServiceMock).fetchDailyData(char1, "test-api-key");
            verify(inspectionServiceMock, never()).fetchDailyData(eq(noKeyChar), anyString());
            verify(inspectionServiceMock).fetchDailyData(char3, "test-api-key");
        }

        @Test
        @DisplayName("성공 - 빈 캐릭터 목록이면 조기 반환")
        void success_emptyCharacters() {
            // given
            given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
                    .willReturn(Collections.emptyList());

            // when
            inspectionScheduleService.fetchScheduledInspectionData();

            // then
            verify(inspectionServiceMock, never()).fetchDailyData(any(), anyString());
        }

        @Test
        @DisplayName("성공 - 빈 API 키 문자열도 스킵")
        void success_skipEmptyApiKey() {
            // given
            Member emptyKeyMember = Member.builder()
                    .id(3L)
                    .username("emptykey@test.com")
                    .apiKey("")
                    .build();
            InspectionCharacter emptyKeyChar = InspectionCharacter.builder()
                    .id(11L)
                    .member(emptyKeyMember)
                    .characterName("빈키캐릭터")
                    .isActive(true)
                    .histories(new ArrayList<>())
                    .build();

            given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
                    .willReturn(List.of(emptyKeyChar));

            // when
            inspectionScheduleService.fetchScheduledInspectionData();

            // then
            verify(inspectionServiceMock, never()).fetchDailyData(eq(emptyKeyChar), anyString());
        }
    }

    // =========================================================================
    // 5. enrichWithChangeInfo 최근 2개만 조회 테스트
    // =========================================================================
    @Nested
    @DisplayName("5. enrichWithChangeInfo 최근 2개 레코드 조회")
    class EnrichWithChangeInfoTest {

        @Test
        @DisplayName("성공 - 2개 이상 히스토리에서 변화량 정확히 계산")
        void success_accurateChangeCalculation() {
            // given
            CombatPowerHistory latest = buildHistoryWithItemLevel(char1, 2300.0, 1630.0, 0);
            CombatPowerHistory previous = buildHistoryWithItemLevel(char1, 2200.0, 1620.0, 1);

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(List.of(latest, previous)); // desc 순서
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(previous, latest));

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getCharacter().getCombatPowerChange()).isEqualTo(100.0);
            assertThat(result.getCharacter().getPreviousCombatPower()).isEqualTo(2200.0);
            assertThat(result.getCharacter().getItemLevelChange()).isEqualTo(10.0);
            assertThat(result.getCharacter().getPreviousItemLevel()).isEqualTo(1620.0);
            assertThat(result.getCharacter().getUnchangedDays()).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공 - 히스토리 1개면 이전 전투력 null")
        void success_singleHistory_previousNull() {
            // given
            CombatPowerHistory latest = buildHistory(char1, 2200.0, 0);

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(List.of(latest)); // 1개만
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(latest));

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getCharacter().getPreviousCombatPower()).isNull();
            assertThat(result.getCharacter().getCombatPowerChange()).isEqualTo(0.0);
            assertThat(result.getCharacter().getPreviousItemLevel()).isNull();
            assertThat(result.getCharacter().getItemLevelChange()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("성공 - 히스토리 0개면 기본값 유지")
        void success_noHistory_defaults() {
            // given
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(0L);
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(Collections.emptyList());

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getCharacter().getPreviousCombatPower()).isNull();
            assertThat(result.getCharacter().getCombatPowerChange()).isEqualTo(0.0);
            assertThat(result.getCharacter().getPreviousItemLevel()).isNull();
            assertThat(result.getCharacter().getItemLevelChange()).isEqualTo(0.0);
            assertThat(result.getCharacter().getUnchangedDays()).isEqualTo(0L);
            assertThat(result.getHistories()).isEmpty();
        }
    }

    // =========================================================================
    // 6. getDetail Fetch Join 테스트
    // =========================================================================
    @Nested
    @DisplayName("6. getDetail 아크그리드 효과 Fetch Join")
    class GetDetailFetchJoinTest {

        @Test
        @DisplayName("성공 - 아크그리드 효과 포함하여 조회")
        void success_withArkgridEffects() {
            // given
            CombatPowerHistory historyWithEffects = buildHistory(char1, 2200.0, 0);
            ArkgridEffectHistory effect1 = ArkgridEffectHistory.builder()
                    .effectName("공격력 증가")
                    .effectLevel(5)
                    .effectTooltip("공격력이 증가합니다.")
                    .build();
            ArkgridEffectHistory effect2 = ArkgridEffectHistory.builder()
                    .effectName("치명 증가")
                    .effectLevel(3)
                    .effectTooltip("치명이 증가합니다.")
                    .build();
            historyWithEffects.replaceArkgridEffects(List.of(effect1, effect2));

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(historyWithEffects));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(List.of(historyWithEffects));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getHistories()).hasSize(1);
            assertThat(result.getHistories().get(0).getArkgridEffects()).hasSize(2);
            assertThat(result.getHistories().get(0).getArkgridEffects().get(0).getEffectName())
                    .isEqualTo("공격력 증가");
            assertThat(result.getHistories().get(0).getArkgridEffects().get(1).getEffectName())
                    .isEqualTo("치명 증가");
        }

        @Test
        @DisplayName("성공 - 아크그리드 효과 없는 히스토리는 빈 리스트")
        void success_noArkgridEffects() {
            // given
            CombatPowerHistory historyNoEffects = buildHistory(char1, 2200.0, 0);

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(historyNoEffects));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(List.of(historyNoEffects));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getHistories()).hasSize(1);
            assertThat(result.getHistories().get(0).getArkgridEffects()).isEmpty();
        }

        @Test
        @DisplayName("성공 - 여러 날짜의 히스토리 각각 아크그리드 효과 포함")
        void success_multipleHistoriesWithEffects() {
            // given
            CombatPowerHistory h1 = buildHistory(char1, 2200.0, 1);
            h1.replaceArkgridEffects(List.of(
                    ArkgridEffectHistory.builder().effectName("효과A").effectLevel(1).build()
            ));
            CombatPowerHistory h2 = buildHistory(char1, 2300.0, 0);
            h2.replaceArkgridEffects(List.of(
                    ArkgridEffectHistory.builder().effectName("효과A").effectLevel(2).build(),
                    ArkgridEffectHistory.builder().effectName("효과B").effectLevel(1).build()
            ));

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(char1));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(h1, h2));
            given(combatPowerHistoryRepository.findLatest2(1L))
                    .willReturn(List.of(h2, h1));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(1L);

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result.getHistories()).hasSize(2);
            assertThat(result.getHistories().get(0).getArkgridEffects()).hasSize(1);
            assertThat(result.getHistories().get(1).getArkgridEffects()).hasSize(2);
        }
    }

    // =========================================================================
    // 7. create() exists 쿼리 테스트
    // =========================================================================
    @Nested
    @DisplayName("7. create() existsByMemberAndCharacterName 쿼리")
    class CreateExistsQueryTest {

        @Test
        @DisplayName("성공 - 새 캐릭터 정상 등록")
        void success_newCharacter() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.existsByMemberAndCharacterName(testMember, "캐릭터1"))
                    .willReturn(false);
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(Collections.emptyList());
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("캐릭터1");
            request.setNoChangeThreshold(3);

            // when
            InspectionCharacterResponse response = inspectionService.create("test@test.com", request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getCharacterName()).isEqualTo("캐릭터1");
            verify(inspectionCharacterRepository).save(any(InspectionCharacter.class));
            // existsByMemberAndCharacterName이 호출되었는지 확인
            verify(inspectionCharacterRepository).existsByMemberAndCharacterName(testMember, "캐릭터1");
            // findByMember와 findByIdAndUsername(0,...) 은 호출되지 않아야 함
            verify(inspectionCharacterRepository, never()).findByMember(any());
            verify(inspectionCharacterRepository, never()).findByIdAndUsername(eq(0L), anyString());
        }

        @Test
        @DisplayName("실패 - 중복 캐릭터 ConditionNotMetException")
        void fail_duplicateCharacter() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.existsByMemberAndCharacterName(testMember, "캐릭터1"))
                    .willReturn(true);

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("캐릭터1");

            // when & then
            assertThatThrownBy(() -> inspectionService.create("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("이미 등록된 캐릭터입니다");

            // API 호출이 일어나지 않아야 함
            verify(lostarkCharacterApiClient, never()).getCharacterProfileForInspection(anyString(), anyString());
            verify(inspectionCharacterRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - API 키 없음 ConditionNotMetException")
        void fail_noApiKey() {
            // given
            Member noKeyMember = Member.builder()
                    .id(2L)
                    .username("nokey@test.com")
                    .build();
            given(memberService.get("nokey@test.com")).willReturn(noKeyMember);

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("캐릭터1");

            // when & then
            assertThatThrownBy(() -> inspectionService.create("nokey@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("API 키가 등록되어 있지 않습니다");

            // exists 쿼리조차 호출되지 않아야 함
            verify(inspectionCharacterRepository, never()).existsByMemberAndCharacterName(any(), anyString());
        }

        @Test
        @DisplayName("성공 - 아크그리드 효과와 함께 초기 히스토리 저장")
        void success_withInitialArkgridEffects() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.existsByMemberAndCharacterName(testMember, "캐릭터1"))
                    .willReturn(false);
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("캐릭터1", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("캐릭터1", "test-api-key"))
                    .willReturn(List.of(new ArkgridEffectDto("공격력 증가", 5, "공격력이 증가합니다.")));
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("캐릭터1");
            request.setNoChangeThreshold(3);

            // when
            InspectionCharacterResponse response = inspectionService.create("test@test.com", request);

            // then
            assertThat(response).isNotNull();
            verify(lostarkCharacterApiClient).getArkgridEffects("캐릭터1", "test-api-key");
            // 히스토리 저장 시 save가 호출됨 (새 히스토리)
            verify(combatPowerHistoryRepository).save(any(CombatPowerHistory.class));
        }
    }

    // =========================================================================
    // Helper methods
    // =========================================================================
    private CombatPowerHistory buildHistory(InspectionCharacter character, double combatPower, int daysAgo) {
        return CombatPowerHistory.builder()
                .id((long) (Math.random() * 10000))
                .inspectionCharacter(character)
                .recordDate(LocalDate.now().minusDays(daysAgo))
                .combatPower(combatPower)
                .itemLevel(character.getItemLevel())
                .characterImage(character.getCharacterImage())
                .arkgridEffects(new ArrayList<>())
                .equipments(new ArrayList<>())
                .engravings(new ArrayList<>())
                .cards(new ArrayList<>())
                .cardSetEffects(new ArrayList<>())
                .gems(new ArrayList<>())
                .arkPassivePoints(new ArrayList<>())
                .arkPassiveEffects(new ArrayList<>())
                .build();
    }

    private CombatPowerHistory buildHistoryWithItemLevel(InspectionCharacter character,
                                                          double combatPower, double itemLevel, int daysAgo) {
        return CombatPowerHistory.builder()
                .id((long) (Math.random() * 10000))
                .inspectionCharacter(character)
                .recordDate(LocalDate.now().minusDays(daysAgo))
                .combatPower(combatPower)
                .itemLevel(itemLevel)
                .characterImage(character.getCharacterImage())
                .arkgridEffects(new ArrayList<>())
                .equipments(new ArrayList<>())
                .engravings(new ArrayList<>())
                .cards(new ArrayList<>())
                .cardSetEffects(new ArrayList<>())
                .gems(new ArrayList<>())
                .arkPassivePoints(new ArrayList<>())
                .arkPassiveEffects(new ArrayList<>())
                .build();
    }
}
