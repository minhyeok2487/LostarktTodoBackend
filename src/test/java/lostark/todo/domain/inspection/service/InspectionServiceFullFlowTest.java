package lostark.todo.domain.inspection.service;

import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.*;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
@DisplayName("InspectionService 전체 데이터 흐름 테스트")
class InspectionServiceFullFlowTest {

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

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private InspectionService inspectionService;

    private Member testMember;
    private InspectionCharacter testCharacter;
    private CharacterJsonDto testProfile;

    // 신규 API 응답 데이터
    private List<ArkgridEffectDto> testArkgridEffects;
    private List<EquipmentDto> testEquipments;
    private List<EngravingDto> testEngravings;
    private CardApiResponse testCardResponse;
    private List<GemDto> testGems;
    private ArkPassiveApiResponse testArkPassiveResponse;

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
        testProfile.setCharacterImage("https://img.test.com/char_new.png");
        testProfile.setItemAvgLevel(1640.0);
        testProfile.setCombatPower(2350.0);
        testProfile.setTitle("모험가");
        testProfile.setGuildName("테스트길드");
        testProfile.setTownName("테스트영지");
        testProfile.setTownLevel(60);
        testProfile.setExpeditionLevel(250);
        testProfile.setStats(List.of(
                createStat("치명", "800"),
                createStat("특화", "1600"),
                createStat("신속", "500")
        ));

        // 아크그리드
        testArkgridEffects = List.of(
                new ArkgridEffectDto("공격력 증가", 5, "공격력이 5% 증가합니다."),
                new ArkgridEffectDto("치명타 증가", 3, "치명타가 3% 증가합니다.")
        );

        // 장비
        testEquipments = List.of(
                new EquipmentDto("무기", "+25 운명의 업화 롱 스태프", "weapon_icon", "에스더", null),
                new EquipmentDto("투구", "+25 운명의 업화 배틀 헬멧", "helmet_icon", "에스더", null)
        );

        // 각인
        testEngravings = List.of(
                new EngravingDto("원한", 3, "유물", null, "적에게 주는 피해 +20%"),
                new EngravingDto("슈퍼 차지", 3, "유물", null, "차지 스킬 피해 +20%"),
                new EngravingDto("방어력 감소", 1, "유물", 7, "방어력 -5%")
        );

        // 카드
        List<CardDto> cards = List.of(
                new CardDto(0, "카드1", "icon1", 5, 5, "전설"),
                new CardDto(1, "카드2", "icon2", 5, 5, "전설"),
                new CardDto(2, "카드3", "icon3", 5, 5, "전설"),
                new CardDto(3, "카드4", "icon4", 3, 5, "전설"),
                new CardDto(4, "카드5", "icon5", 5, 5, "전설"),
                new CardDto(5, "카드6", "icon6", 5, 5, "전설")
        );
        List<CardSetEffectDto> cardSetEffects = List.of(
                new CardSetEffectDto("남겨진 바람의 절벽 6세트", "암속성 피해 감소 +30%")
        );
        testCardResponse = new CardApiResponse(cards, cardSetEffects);

        // 보석
        testGems = List.of(
                new GemDto("잔혈 폭격", 0, "skill_icon1", 10, "유물", "피해 +21%", "멸화"),
                new GemDto("회전 베기", 1, "skill_icon2", 10, "유물", "재사용 대기시간 -18%", "홍염")
        );

        // 아크패시브
        List<ArkPassivePointDto> arkPoints = List.of(
                new ArkPassivePointDto("깨달음", 30, "깨달음 포인트"),
                new ArkPassivePointDto("도약", 25, "도약 포인트"),
                new ArkPassivePointDto("진화", 20, "진화 포인트")
        );
        List<ArkPassiveEffectDto> arkEffects = List.of(
                new ArkPassiveEffectDto("깨달음", "공격력 증가", "icon1", 1, 5),
                new ArkPassiveEffectDto("도약", "치명 증가", "icon2", 2, 3),
                new ArkPassiveEffectDto("진화", "이동속도", "icon3", 3, 2)
        );
        testArkPassiveResponse = new ArkPassiveApiResponse("아크패시브 타이틀", arkPoints, arkEffects);

        // 공통 스텁
        given(lostarkCharacterApiClient.getCharacterProfileForInspection(anyString(), anyString()))
                .willReturn(testProfile);
        given(lostarkCharacterApiClient.getArkgridEffects(anyString(), anyString()))
                .willReturn(testArkgridEffects);
        given(lostarkCharacterApiClient.getEquipment(anyString(), anyString()))
                .willReturn(testEquipments);
        given(lostarkCharacterApiClient.getEngravings(anyString(), anyString()))
                .willReturn(testEngravings);
        given(lostarkCharacterApiClient.getCards(anyString(), anyString()))
                .willReturn(testCardResponse);
        given(lostarkCharacterApiClient.getGems(anyString(), anyString()))
                .willReturn(testGems);
        given(lostarkCharacterApiClient.getArkPassive(anyString(), anyString()))
                .willReturn(testArkPassiveResponse);
    }

    private CharacterJsonDto.StatDto createStat(String type, String value) {
        CharacterJsonDto.StatDto stat = new CharacterJsonDto.StatDto();
        stat.setType(type);
        stat.setValue(value);
        return stat;
    }

    @Nested
    @DisplayName("fetchDailyData - 전체 API 데이터 흐름")
    class FetchDailyDataFullFlowTest {

        @Test
        @DisplayName("신규 히스토리 생성 시 8개 컬렉션 모두 저장된다")
        void newHistory_savesAllCollections() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            CombatPowerHistory saved = captor.getValue();

            assertThat(saved.getCombatPower()).isEqualTo(2350.0);
            assertThat(saved.getItemLevel()).isEqualTo(1640.0);
            assertThat(saved.getRecordDate()).isEqualTo(LocalDate.now());
            assertThat(saved.getStatsJson()).isNotNull();
            assertThat(saved.getStatsJson()).contains("치명");

            // 8개 컬렉션 검증
            assertThat(saved.getArkgridEffects()).hasSize(2);
            assertThat(saved.getEquipments()).hasSize(2);
            assertThat(saved.getEngravings()).hasSize(3);
            assertThat(saved.getCards()).hasSize(6);
            assertThat(saved.getCardSetEffects()).hasSize(1);
            assertThat(saved.getGems()).hasSize(2);
            assertThat(saved.getArkPassivePoints()).hasSize(3);
            assertThat(saved.getArkPassiveEffects()).hasSize(3);

            // 아크패시브 타이틀
            assertThat(saved.getArkPassiveTitle()).isEqualTo("아크패시브 타이틀");
        }

        @Test
        @DisplayName("기존 히스토리 업데이트 시 모든 컬렉션이 교체된다")
        void existingHistory_replacesAllCollections() {
            // given
            CombatPowerHistory existing = CombatPowerHistory.builder()
                    .id(10L)
                    .inspectionCharacter(testCharacter)
                    .recordDate(LocalDate.now())
                    .combatPower(2200.0)
                    .itemLevel(1620.0)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            // 기존 데이터 추가
            ArkgridEffectHistory oldEffect = ArkgridEffectHistory.builder()
                    .effectName("구 효과").effectLevel(1).build();
            oldEffect.setCombatPowerHistory(existing);
            existing.getArkgridEffects().add(oldEffect);

            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.of(existing));
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.of(existing));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then - save 호출 안 됨 (기존 업데이트)
            verify(combatPowerHistoryRepository, never()).save(any());

            // 기존 히스토리의 데이터가 업데이트됨
            assertThat(existing.getCombatPower()).isEqualTo(2350.0);
            assertThat(existing.getArkgridEffects()).hasSize(2);
            assertThat(existing.getArkgridEffects().get(0).getEffectName()).isEqualTo("공격력 증가");
            assertThat(existing.getEquipments()).hasSize(2);
            assertThat(existing.getEngravings()).hasSize(3);
            assertThat(existing.getCards()).hasSize(6);
            assertThat(existing.getCardSetEffects()).hasSize(1);
            assertThat(existing.getGems()).hasSize(2);
            assertThat(existing.getArkPassivePoints()).hasSize(3);
            assertThat(existing.getArkPassiveEffects()).hasSize(3);
        }

        @Test
        @DisplayName("캐릭터 프로필 정보가 업데이트된다 (칭호, 길드, 영지 포함)")
        void updatesCharacterProfile() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            assertThat(testCharacter.getCombatPower()).isEqualTo(2350.0);
            assertThat(testCharacter.getItemLevel()).isEqualTo(1640.0);
            assertThat(testCharacter.getCharacterImage()).isEqualTo("https://img.test.com/char_new.png");
            assertThat(testCharacter.getTitle()).isEqualTo("모험가");
            assertThat(testCharacter.getGuildName()).isEqualTo("테스트길드");
            assertThat(testCharacter.getTownName()).isEqualTo("테스트영지");
            assertThat(testCharacter.getTownLevel()).isEqualTo(60);
            assertThat(testCharacter.getExpeditionLevel()).isEqualTo(250);
        }

        @Test
        @DisplayName("각인 데이터가 올바르게 매핑된다")
        void engravingDataMappedCorrectly() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            List<EngravingHistory> engravings = captor.getValue().getEngravings();

            assertThat(engravings).hasSize(3);
            assertThat(engravings.get(0).getName()).isEqualTo("원한");
            assertThat(engravings.get(0).getLevel()).isEqualTo(3);
            assertThat(engravings.get(0).getGrade()).isEqualTo("유물");
            assertThat(engravings.get(0).getDescription()).isEqualTo("적에게 주는 피해 +20%");
            assertThat(engravings.get(2).getName()).isEqualTo("방어력 감소");
            assertThat(engravings.get(2).getAbilityStoneLevel()).isEqualTo(7);
        }

        @Test
        @DisplayName("카드 데이터가 올바르게 매핑된다 (카드 + 세트효과)")
        void cardDataMappedCorrectly() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            CombatPowerHistory saved = captor.getValue();

            List<CardHistory> cards = saved.getCards();
            assertThat(cards).hasSize(6);
            assertThat(cards.get(0).getSlot()).isEqualTo(0);
            assertThat(cards.get(0).getName()).isEqualTo("카드1");
            assertThat(cards.get(0).getAwakeCount()).isEqualTo(5);
            assertThat(cards.get(3).getAwakeCount()).isEqualTo(3);

            List<CardSetEffectHistory> setEffects = saved.getCardSetEffects();
            assertThat(setEffects).hasSize(1);
            assertThat(setEffects.get(0).getName()).contains("바람의 절벽");
        }

        @Test
        @DisplayName("보석 데이터가 올바르게 매핑된다")
        void gemDataMappedCorrectly() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            List<GemHistory> gems = captor.getValue().getGems();

            assertThat(gems).hasSize(2);
            assertThat(gems.get(0).getSkillName()).isEqualTo("잔혈 폭격");
            assertThat(gems.get(0).getGemSlot()).isEqualTo(0);
            assertThat(gems.get(0).getLevel()).isEqualTo(10);
            assertThat(gems.get(0).getGrade()).isEqualTo("유물");
            assertThat(gems.get(0).getOption()).isEqualTo("멸화");
            assertThat(gems.get(1).getOption()).isEqualTo("홍염");
        }

        @Test
        @DisplayName("아크패시브 데이터가 올바르게 매핑된다 (포인트 + 효과 + 타이틀)")
        void arkPassiveDataMappedCorrectly() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            CombatPowerHistory saved = captor.getValue();

            assertThat(saved.getArkPassiveTitle()).isEqualTo("아크패시브 타이틀");

            List<ArkPassivePointHistory> points = saved.getArkPassivePoints();
            assertThat(points).hasSize(3);
            assertThat(points.get(0).getName()).isEqualTo("깨달음");
            assertThat(points.get(0).getValue()).isEqualTo(30);
            assertThat(points.get(1).getName()).isEqualTo("도약");
            assertThat(points.get(2).getName()).isEqualTo("진화");

            List<ArkPassiveEffectHistory> effects = saved.getArkPassiveEffects();
            assertThat(effects).hasSize(3);
            assertThat(effects.get(0).getCategory()).isEqualTo("깨달음");
            assertThat(effects.get(0).getTier()).isEqualTo(1);
            assertThat(effects.get(1).getCategory()).isEqualTo("도약");
            assertThat(effects.get(1).getTier()).isEqualTo(2);
            assertThat(effects.get(2).getCategory()).isEqualTo("진화");
            assertThat(effects.get(2).getTier()).isEqualTo(3);
        }

        @Test
        @DisplayName("statsJson에 스탯 데이터가 직렬화된다")
        void statsJsonSerialized() {
            // given
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            String statsJson = captor.getValue().getStatsJson();

            assertThat(statsJson).isNotNull();
            assertThat(statsJson).contains("치명");
            assertThat(statsJson).contains("800");
            assertThat(statsJson).contains("특화");
            assertThat(statsJson).contains("1600");
        }
    }

    @Nested
    @DisplayName("fetchDailyData - 알림 흐름")
    class FetchDailyDataNotificationTest {

        @Test
        @DisplayName("전투력 증가 + 장비 변화 시 둘 다 알림 발송")
        void combatPowerIncrease_andEquipmentChange() {
            // given
            testCharacter.setCombatPower(2200.0);
            testProfile.setCombatPower(2350.0);

            EquipmentHistory prevWeapon = new EquipmentHistory();
            prevWeapon.setType("무기");
            prevWeapon.setName("+24 운명의 업화 롱 스태프");
            prevWeapon.setRefinement(24);

            CombatPowerHistory prevHistory = CombatPowerHistory.builder()
                    .id(5L)
                    .inspectionCharacter(testCharacter)
                    .combatPower(2200.0)
                    .recordDate(LocalDate.now().minusDays(1))
                    .equipments(new ArrayList<>(List.of(prevWeapon)))
                    .arkgridEffects(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.of(prevHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then - 전투력 증가 알림
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("전투력이 증가했습니다"), eq(1L));

            // 장비 변화 알림 (무기 이름이 다르므로 교체 감지)
            verify(notificationService, atLeast(2)).createInspectionNotification(
                    eq(testMember), anyString(), eq(1L));
        }

        @Test
        @DisplayName("무변동 임계값 초과 시 알림 발송")
        void noChangeThresholdExceeded() {
            // given
            testCharacter.setCombatPower(2200.0);
            testCharacter.setNoChangeThreshold(3);
            testProfile.setCombatPower(2200.0); // 변동 없음

            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(5L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(notificationService).createInspectionNotification(
                    eq(testMember), contains("변동이 없습니다"), eq(1L));
        }

        @Test
        @DisplayName("무변동 임계값 미만이면 알림 미발송")
        void belowNoChangeThreshold_noNotification() {
            // given
            testCharacter.setCombatPower(2200.0);
            testCharacter.setNoChangeThreshold(3);
            testProfile.setCombatPower(2200.0);

            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(2L);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then - 전투력 변동도 없고, 무변동 임계값도 미달이므로 알림 없음
            verify(notificationService, never()).createInspectionNotification(
                    any(), anyString(), anyLong());
        }
    }

    @Nested
    @DisplayName("fetchDailyData - 에러 처리")
    class FetchDailyDataErrorTest {

        @Test
        @DisplayName("API 호출 실패 시 예외가 전파되지 않음")
        void apiFailure_doesNotPropagate() {
            // given
            given(lostarkCharacterApiClient.getCharacterProfileForInspection(anyString(), anyString()))
                    .willThrow(new RuntimeException("API 서버 다운"));

            // when - 예외 전파 없어야 함
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(notificationService, never()).createInspectionNotification(any(), anyString(), anyLong());
            verify(combatPowerHistoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 스탯 리스트일 때 statsJson이 null")
        void emptyStats_nullStatsJson() {
            // given
            testProfile.setStats(null);

            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            ArgumentCaptor<CombatPowerHistory> captor = ArgumentCaptor.forClass(CombatPowerHistory.class);

            // when
            inspectionService.fetchDailyData(testCharacter, "test-api-key");

            // then
            verify(combatPowerHistoryRepository).save(captor.capture());
            assertThat(captor.getValue().getStatsJson()).isNull();
        }
    }

    @Nested
    @DisplayName("create - 전체 API 데이터 흐름")
    class CreateFullFlowTest {

        @Test
        @DisplayName("캐릭터 등록 시 모든 API 데이터로 초기 히스토리 저장")
        void createsInitialHistoryWithAllData() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.existsByMemberAndCharacterName(testMember, "테스트캐릭터"))
                    .willReturn(false);
            given(lostarkCharacterApiClient.getCharacterProfileForInspection("테스트캐릭터", "test-api-key"))
                    .willReturn(testProfile);
            given(lostarkCharacterApiClient.getArkgridEffects("테스트캐릭터", "test-api-key"))
                    .willReturn(testArkgridEffects);

            CreateInspectionCharacterRequest request = new CreateInspectionCharacterRequest();
            request.setCharacterName("테스트캐릭터");
            request.setNoChangeThreshold(3);

            // when
            InspectionCharacterResponse response = inspectionService.create("test@test.com", request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getCharacterName()).isEqualTo("테스트캐릭터");
            assertThat(response.getTitle()).isEqualTo("모험가");
            assertThat(response.getGuildName()).isEqualTo("테스트길드");
            assertThat(response.getTownName()).isEqualTo("테스트영지");
            assertThat(response.getTownLevel()).isEqualTo(60);
            assertThat(response.getExpeditionLevel()).isEqualTo(250);

            // 히스토리 저장 확인
            verify(combatPowerHistoryRepository).save(any(CombatPowerHistory.class));
        }
    }

    @Nested
    @DisplayName("getAll - 다중 캐릭터 + 변화량")
    class GetAllMultiCharacterTest {

        @Test
        @DisplayName("여러 캐릭터의 변화량 정보가 올바르게 계산된다")
        void multipleCharacters_withChangeInfo() {
            // given
            InspectionCharacter char2 = InspectionCharacter.builder()
                    .id(2L)
                    .member(testMember)
                    .characterName("캐릭터2")
                    .serverName("루페온")
                    .characterClassName("워로드")
                    .itemLevel(1600.0)
                    .combatPower(2000.0)
                    .noChangeThreshold(3)
                    .isActive(true)
                    .histories(new ArrayList<>())
                    .build();

            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember))
                    .willReturn(List.of(testCharacter, char2));

            // 캐릭터1: 전투력 2200 → 2350 (150 증가)
            CombatPowerHistory h1Latest = CombatPowerHistory.builder()
                    .inspectionCharacter(testCharacter).combatPower(2350.0).itemLevel(1640.0)
                    .recordDate(LocalDate.now()).arkgridEffects(new ArrayList<>()).build();
            CombatPowerHistory h1Previous = CombatPowerHistory.builder()
                    .inspectionCharacter(testCharacter).combatPower(2200.0).itemLevel(1620.0)
                    .recordDate(LocalDate.now().minusDays(1)).arkgridEffects(new ArrayList<>()).build();

            // 캐릭터2: 전투력 변동 없음
            CombatPowerHistory h2Latest = CombatPowerHistory.builder()
                    .inspectionCharacter(char2).combatPower(2000.0).itemLevel(1600.0)
                    .recordDate(LocalDate.now()).arkgridEffects(new ArrayList<>()).build();
            CombatPowerHistory h2Previous = CombatPowerHistory.builder()
                    .inspectionCharacter(char2).combatPower(2000.0).itemLevel(1600.0)
                    .recordDate(LocalDate.now().minusDays(1)).arkgridEffects(new ArrayList<>()).build();

            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Map.of(
                            1L, List.of(h1Latest, h1Previous),
                            2L, List.of(h2Latest, h2Previous)
                    ));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Map.of(1L, 0L, 2L, 5L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(2);

            InspectionCharacterResponse resp1 = result.stream()
                    .filter(r -> r.getCharacterName().equals("테스트캐릭터")).findFirst().orElseThrow();
            assertThat(resp1.getCombatPowerChange()).isEqualTo(150.0);
            assertThat(resp1.getItemLevelChange()).isEqualTo(20.0);
            assertThat(resp1.getUnchangedDays()).isEqualTo(0L);

            InspectionCharacterResponse resp2 = result.stream()
                    .filter(r -> r.getCharacterName().equals("캐릭터2")).findFirst().orElseThrow();
            assertThat(resp2.getCombatPowerChange()).isEqualTo(0.0);
            assertThat(resp2.getUnchangedDays()).isEqualTo(5L);
        }

        @Test
        @DisplayName("히스토리가 1개뿐인 캐릭터는 변화량이 0")
        void singleHistory_zeroChange() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByMember(testMember))
                    .willReturn(List.of(testCharacter));

            CombatPowerHistory onlyHistory = CombatPowerHistory.builder()
                    .inspectionCharacter(testCharacter).combatPower(2200.0).itemLevel(1620.0)
                    .recordDate(LocalDate.now()).arkgridEffects(new ArrayList<>()).build();

            given(combatPowerHistoryRepository.findLatest2ByCharacterIds(anyList()))
                    .willReturn(Map.of(1L, List.of(onlyHistory)));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(anyList()))
                    .willReturn(Map.of(1L, 1L));

            // when
            List<InspectionCharacterResponse> result = inspectionService.getAll("test@test.com");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCombatPowerChange()).isEqualTo(0.0);
            assertThat(result.get(0).getPreviousCombatPower()).isNull();
        }
    }

    @Nested
    @DisplayName("getDetail - 상세 조회")
    class GetDetailExtendedTest {

        @Test
        @DisplayName("히스토리 응답에 모든 컬렉션 데이터가 포함된다")
        void historyResponse_containsAllCollections() {
            // given
            CombatPowerHistory richHistory = CombatPowerHistory.builder()
                    .id(1L)
                    .inspectionCharacter(testCharacter)
                    .recordDate(LocalDate.now())
                    .combatPower(2350.0)
                    .itemLevel(1640.0)
                    .statsJson("[{\"type\":\"치명\",\"value\":\"800\"}]")
                    .arkPassiveTitle("테스트 타이틀")
                    .townName("영지")
                    .townLevel(60)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            // 각인 추가
            EngravingHistory eng = EngravingHistory.builder().name("원한").level(3).grade("유물").build();
            eng.setCombatPowerHistory(richHistory);
            richHistory.getEngravings().add(eng);

            // 보석 추가
            GemHistory gem = GemHistory.builder().skillName("잔혈 폭격").gemSlot(0).level(10).build();
            gem.setCombatPowerHistory(richHistory);
            richHistory.getGems().add(gem);

            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));
            given(combatPowerHistoryRepository.findByCharacterAndDateRange(eq(1L), any(), any()))
                    .willReturn(List.of(richHistory));
            given(combatPowerHistoryRepository.findLatest2(1L)).willReturn(List.of(richHistory));
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(1L)).willReturn(0L);

            // when
            InspectionDashboardResponse result = inspectionService.getDetail(
                    "test@test.com", 1L, LocalDate.now().minusDays(30), LocalDate.now());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getHistories()).hasSize(1);

            CombatPowerHistoryResponse histResp = result.getHistories().get(0);
            assertThat(histResp.getCombatPower()).isEqualTo(2350.0);
            assertThat(histResp.getStatsJson()).contains("치명");
            assertThat(histResp.getArkPassiveTitle()).isEqualTo("테스트 타이틀");
            assertThat(histResp.getEngravings()).hasSize(1);
            assertThat(histResp.getEngravings().get(0).getName()).isEqualTo("원한");
            assertThat(histResp.getGems()).hasSize(1);
            assertThat(histResp.getGems().get(0).getSkillName()).isEqualTo("잔혈 폭격");
        }
    }

    @Nested
    @DisplayName("refresh - 수동 새로고침")
    class RefreshExtendedTest {

        @Test
        @DisplayName("수동 새로고침 후 최신 데이터가 반영된 상세 응답 반환")
        void refreshReturnsUpdatedDetail() {
            // given
            given(memberService.get("test@test.com")).willReturn(testMember);
            given(inspectionCharacterRepository.findByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));
            given(combatPowerHistoryRepository.findByCharacterAndDate(anyLong(), any()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.findLatest(anyLong()))
                    .willReturn(Optional.empty());
            given(combatPowerHistoryRepository.countConsecutiveUnchangedDays(anyLong())).willReturn(0L);

            CombatPowerHistory newHistory = CombatPowerHistory.builder()
                    .id(100L)
                    .inspectionCharacter(testCharacter)
                    .recordDate(LocalDate.now())
                    .combatPower(2350.0)
                    .itemLevel(1640.0)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();

            given(combatPowerHistoryRepository.findByCharacterAndDateRange(anyLong(), any(), any()))
                    .willReturn(List.of(newHistory));
            given(combatPowerHistoryRepository.findLatest2(anyLong())).willReturn(List.of(newHistory));

            // when
            InspectionDashboardResponse result = inspectionService.refresh("test@test.com", 1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCharacter()).isNotNull();
            assertThat(result.getHistories()).isNotEmpty();
        }

        @Test
        @DisplayName("API 키 없는 유저가 새로고침하면 예외")
        void noApiKey_throwsException() {
            // given
            Member noKeyMember = Member.builder()
                    .id(2L).username("nokey@test.com").build();
            given(memberService.get("nokey@test.com")).willReturn(noKeyMember);

            // when & then
            assertThatThrownBy(() -> inspectionService.refresh("nokey@test.com", 1L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("API 키가 등록되어 있지 않습니다");
        }
    }
}
