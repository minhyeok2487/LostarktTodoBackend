package lostark.todo.domain.character.service;

import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.entity.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.enums.DayTodoCategoryEnum;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.character.repository.TodoV2Repository;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.content.service.ContentService;
import lostark.todo.domain.logs.service.LogService;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private TodoV2Repository todoV2Repository;

    @Mock
    private LostarkCharacterApiClient lostarkCharacterApiClient;

    @Mock
    private ContentService contentService;

    @Mock
    private MarketService marketService;

    @Mock
    private LogService logService;

    @InjectMocks
    private CharacterService characterService;

    private Member testMember;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .mainCharacter("테스트캐릭터")
                .build();

        testCharacter = createTestCharacter();
    }

    private Character createTestCharacter() {
        DayTodo dayTodo = DayTodo.builder()
                .chaosCheck(0)
                .chaosGauge(100)
                .chaosGold(1000.0)
                .guardianCheck(0)
                .guardianGauge(40)
                .guardianGold(500.0)
                .eponaCheck2(0)
                .eponaGauge(60)
                .beforeChaosGauge(100)
                .beforeGuardianGauge(40)
                .beforeEponaGauge(60)
                .weekTotalGold(0)
                .build();

        Settings settings = new Settings();
        WeekTodo weekTodo = WeekTodo.builder()
                .weekEpona(0)
                .silmaelChange(false)
                .cubeTicket(0)
                .elysianCount(0)
                .build();

        return Character.builder()
                .id(1L)
                .characterName("테스트캐릭터")
                .serverName("루페온")
                .itemLevel(1620.0)
                .characterLevel(70)
                .characterClassName("버서커")
                .combatPower(50000.0)
                .goldCharacter(false)
                .dayTodo(dayTodo)
                .weekTodo(weekTodo)
                .todoV2List(new ArrayList<>())
                .raidBusGoldList(new ArrayList<>())
                .settings(settings)
                .isDeleted(false)
                .member(testMember)
                .sortNumber(0)
                .build();
    }

    @Nested
    @DisplayName("get 메서드")
    class GetTest {

        @Test
        @DisplayName("성공 - 캐릭터 조회")
        void success() {
            // given
            given(characterRepository.getByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));

            // when
            Character result = characterService.get(1L, "test@test.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCharacterName()).isEqualTo("테스트캐릭터");
            verify(characterRepository).getByIdAndUsername(1L, "test@test.com");
        }

        @Test
        @DisplayName("실패 - 캐릭터가 존재하지 않음")
        void fail_characterNotFound() {
            // given
            given(characterRepository.getByIdAndUsername(anyLong(), anyString()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> characterService.get(999L, "test@test.com"))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("캐릭터가 존재하지 않습니다");
        }
    }

    @Nested
    @DisplayName("updateGoldCharacter 메서드")
    class UpdateGoldCharacterTest {

        @Test
        @DisplayName("성공 - 골드 캐릭터 활성화")
        void success_enableGoldCharacter() {
            // given
            assertThat(testCharacter.isGoldCharacter()).isFalse();

            // when
            characterService.updateGoldCharacter(testCharacter);

            // then
            assertThat(testCharacter.isGoldCharacter()).isTrue();
        }

        @Test
        @DisplayName("성공 - 골드 캐릭터 비활성화")
        void success_disableGoldCharacter() {
            // given
            testCharacter.setGoldCharacter(true);

            // when
            characterService.updateGoldCharacter(testCharacter);

            // then
            assertThat(testCharacter.isGoldCharacter()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateRaidGoldCheck 메서드")
    class UpdateRaidGoldCheckTest {

        @Test
        @DisplayName("성공 - 레이드 골드 체크 활성화")
        void success() {
            // given
            WeekContent weekContent = WeekContent.builder()
                    .id(1L)
                    .weekCategory("발탄")
                    .build();
            TodoV2 todoV2 = TodoV2.builder()
                    .id(1L)
                    .weekContent(weekContent)
                    .goldCheck(false)
                    .character(testCharacter)
                    .build();
            testCharacter.setTodoV2List(List.of(todoV2));

            // when
            characterService.updateRaidGoldCheck(testCharacter, "발탄", true);

            // then
            assertThat(todoV2.isGoldCheck()).isTrue();
        }

        @Test
        @DisplayName("실패 - 골드 획득 3개 초과")
        void fail_exceedGoldLimit() {
            // given
            List<TodoV2> todoV2List = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                WeekContent weekContent = WeekContent.builder()
                        .id((long) i)
                        .weekCategory("레이드" + i)
                        .build();
                TodoV2 todoV2 = TodoV2.builder()
                        .id((long) i)
                        .weekContent(weekContent)
                        .goldCheck(true)
                        .character(testCharacter)
                        .build();
                todoV2List.add(todoV2);
            }
            testCharacter.setTodoV2List(todoV2List);

            // when & then
            assertThatThrownBy(() ->
                    characterService.updateRaidGoldCheck(testCharacter, "새레이드", true))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("골드 획득은 3개까지 가능합니다");
        }

        @Test
        @DisplayName("실패 - 레이드 미등록")
        void fail_raidNotRegistered() {
            // given
            testCharacter.setTodoV2List(new ArrayList<>());

            // when & then
            assertThatThrownBy(() ->
                    characterService.updateRaidGoldCheck(testCharacter, "미등록레이드", true))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("레이드를 먼저 등록해주십시오");
        }
    }

    @Nested
    @DisplayName("updateDayCheck 메서드")
    class UpdateDayCheckTest {

        @Test
        @DisplayName("성공 - 에포나 체크")
        void success_eponaCheck() {
            // given
            UpdateDayCheckRequest request = new UpdateDayCheckRequest();
            request.setCategory(DayTodoCategoryEnum.epona);
            request.setAllCheck(false);

            // when
            CharacterResponse response = characterService.updateDayCheck(testCharacter, request);

            // then
            assertThat(testCharacter.getDayTodo().getEponaCheck2()).isEqualTo(1);
            verify(logService).processDayLog(eq(DayTodoCategoryEnum.epona), any(CharacterResponse.class));
        }

        @Test
        @DisplayName("성공 - 에포나 전체 체크")
        void success_eponaAllCheck() {
            // given
            UpdateDayCheckRequest request = new UpdateDayCheckRequest();
            request.setCategory(DayTodoCategoryEnum.epona);
            request.setAllCheck(true);

            // when
            characterService.updateDayCheck(testCharacter, request);

            // then
            assertThat(testCharacter.getDayTodo().getEponaCheck2()).isEqualTo(3);
        }

        @Test
        @DisplayName("성공 - 카오스 체크")
        void success_chaosCheck() {
            // given
            UpdateDayCheckRequest request = new UpdateDayCheckRequest();
            request.setCategory(DayTodoCategoryEnum.chaos);
            request.setAllCheck(false);

            // when
            characterService.updateDayCheck(testCharacter, request);

            // then
            assertThat(testCharacter.getDayTodo().getChaosCheck()).isEqualTo(2);
            verify(logService).processDayLog(eq(DayTodoCategoryEnum.chaos), any(CharacterResponse.class));
        }

        @Test
        @DisplayName("성공 - 가디언 체크")
        void success_guardianCheck() {
            // given
            UpdateDayCheckRequest request = new UpdateDayCheckRequest();
            request.setCategory(DayTodoCategoryEnum.guardian);
            request.setAllCheck(false);

            // when
            characterService.updateDayCheck(testCharacter, request);

            // then
            assertThat(testCharacter.getDayTodo().getGuardianCheck()).isEqualTo(1);
            verify(logService).processDayLog(eq(DayTodoCategoryEnum.guardian), any(CharacterResponse.class));
        }
    }

    @Nested
    @DisplayName("validateUpdateDayGauge 메서드")
    class ValidateUpdateDayGaugeTest {

        @Test
        @DisplayName("성공 - 유효한 게이지 값")
        void success() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(100);
            request.setGuardianGauge(40);

            // when & then
            assertThatCode(() -> characterService.validateUpdateDayGauge(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 카오스 게이지 범위 초과")
        void fail_chaosGaugeOutOfRange() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(250);
            request.setGuardianGauge(40);

            // when & then
            assertThatThrownBy(() -> characterService.validateUpdateDayGauge(request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("휴식게이지는 0~200 사이이며, 10단위여야 합니다");
        }

        @Test
        @DisplayName("실패 - 가디언 게이지 범위 초과")
        void fail_guardianGaugeOutOfRange() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(100);
            request.setGuardianGauge(150);

            // when & then
            assertThatThrownBy(() -> characterService.validateUpdateDayGauge(request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("휴식게이지는 0~100 사이이며, 10단위여야 합니다");
        }

        @Test
        @DisplayName("실패 - 10단위가 아닌 게이지")
        void fail_gaugeNotMultipleOf10() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(105);
            request.setGuardianGauge(40);

            // when & then
            assertThatThrownBy(() -> characterService.validateUpdateDayGauge(request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("10단위여야 합니다");
        }

        @Test
        @DisplayName("실패 - 음수 게이지")
        void fail_negativeGauge() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(-10);
            request.setGuardianGauge(40);

            // when & then
            assertThatThrownBy(() -> characterService.validateUpdateDayGauge(request))
                    .isInstanceOf(ConditionNotMetException.class);
        }
    }

    @Nested
    @DisplayName("updateDayGauge 메서드")
    class UpdateDayGaugeTest {

        @Test
        @DisplayName("성공 - 게이지 업데이트")
        void success() {
            // given
            UpdateDayGaugeRequest request = new UpdateDayGaugeRequest();
            request.setChaosGauge(120);
            request.setGuardianGauge(60);

            Map<String, Market> marketMap = new HashMap<>();
            Market jewelry = Market.builder().recentPrice(100).build();
            marketMap.put("3티어 1레벨 보석", jewelry);
            marketMap.put("4티어 1레벨 보석", jewelry);
            given(marketService.findLevelUpResource()).willReturn(marketMap);

            DayContent guardian = DayContent.builder()
                    .jewelry(0.5)
                    .build();
            testCharacter.getDayTodo().setGuardian(guardian);

            // when
            characterService.updateDayGauge(testCharacter, request);

            // then
            assertThat(testCharacter.getDayTodo().getChaosGauge()).isEqualTo(120);
            assertThat(testCharacter.getDayTodo().getGuardianGauge()).isEqualTo(60);
            verify(marketService).findLevelUpResource();
        }
    }

    @Nested
    @DisplayName("updateDayCheckAll 메서드")
    class UpdateDayCheckAllTest {

        @Test
        @DisplayName("성공 - 전체 체크 (모두 미체크 상태에서)")
        void success_checkAll() {
            // given
            testCharacter.getDayTodo().setChaosCheck(0);
            testCharacter.getDayTodo().setGuardianCheck(0);
            testCharacter.getSettings().setShowChaos(true);
            testCharacter.getSettings().setShowGuardian(true);

            // when
            CharacterResponse response = characterService.updateDayCheckAll(testCharacter);

            // then
            assertThat(response).isNotNull();
            verify(logService, times(2)).processDayLog(any(DayTodoCategoryEnum.class), any(CharacterResponse.class));
        }

        @Test
        @DisplayName("성공 - 전체 해제 (모두 체크된 상태에서)")
        void success_uncheckAll() {
            // given
            testCharacter.getDayTodo().setChaosCheck(2);
            testCharacter.getDayTodo().setGuardianCheck(1);
            testCharacter.getSettings().setShowChaos(true);
            testCharacter.getSettings().setShowGuardian(true);

            // when
            CharacterResponse response = characterService.updateDayCheckAll(testCharacter);

            // then
            assertThat(response).isNotNull();
            verify(logService, times(2)).processDayLog(any(DayTodoCategoryEnum.class), any(CharacterResponse.class));
        }
    }

    @Nested
    @DisplayName("editSort 메서드")
    class EditSortTest {

        @Test
        @DisplayName("성공 - 캐릭터 정렬 변경")
        void success() {
            // given
            Character character1 = createTestCharacter();
            character1.setCharacterName("캐릭터1");
            character1.setSortNumber(0);

            Character character2 = createTestCharacter();
            character2.setId(2L);
            character2.setCharacterName("캐릭터2");
            character2.setSortNumber(1);

            given(characterRepository.getCharacterList("test@test.com"))
                    .willReturn(List.of(character1, character2));

            List<CharacterSortRequest> sortRequests = List.of(
                    CharacterSortRequest.builder().characterName("캐릭터1").sortNumber(1).build(),
                    CharacterSortRequest.builder().characterName("캐릭터2").sortNumber(0).build()
            );

            // when
            List<CharacterResponse> result = characterService.editSort("test@test.com", sortRequests);

            // then
            assertThat(result).hasSize(2);
            assertThat(character1.getSortNumber()).isEqualTo(1);
            assertThat(character2.getSortNumber()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("성공 - 캐릭터 삭제")
        void success() {
            // given
            testCharacter.setGoldCharacter(false);
            given(characterRepository.getByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));

            // when
            characterService.delete(1L, "test@test.com");

            // then
            assertThat(testCharacter.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("실패 - 골드 획득 캐릭터는 삭제 불가")
        void fail_goldCharacterCannotBeDeleted() {
            // given
            testCharacter.setGoldCharacter(true);
            given(characterRepository.getByIdAndUsername(1L, "test@test.com"))
                    .willReturn(Optional.of(testCharacter));

            // when & then
            assertThatThrownBy(() -> characterService.delete(1L, "test@test.com"))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("골드 획득 캐릭터는 삭제 할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("updateDayCheckAllCharacters 메서드")
    class UpdateDayCheckAllCharactersTest {

        @Test
        @DisplayName("성공 - 전체 캐릭터 일일 체크")
        void success() {
            // given
            Character character1 = createTestCharacter();
            character1.setCharacterName("캐릭터1");
            character1.getSettings().setShowCharacter(true);
            character1.getSettings().setShowChaos(true);
            character1.getSettings().setShowGuardian(true);

            Character character2 = createTestCharacter();
            character2.setId(2L);
            character2.setCharacterName("캐릭터2");
            character2.getSettings().setShowCharacter(true);
            character2.getSettings().setShowChaos(true);
            character2.getSettings().setShowGuardian(true);

            given(characterRepository.getCharacterList("test@test.com"))
                    .willReturn(List.of(character1, character2));

            // when
            UpdateDayCheckAllCharactersResponse response =
                    characterService.updateDayCheckAllCharacters("test@test.com", "전체");

            // then
            assertThat(response).isNotNull();
            assertThat(response.getServerName()).isEqualTo("전체");
            verify(logService).processDayCheckAllCharactersLog(eq("test@test.com"), any());
        }

        @Test
        @DisplayName("성공 - 특정 서버만 일일 체크")
        void success_specificServer() {
            // given
            Character character1 = createTestCharacter();
            character1.setServerName("루페온");
            character1.getSettings().setShowCharacter(true);

            Character character2 = createTestCharacter();
            character2.setId(2L);
            character2.setServerName("아브렐슈드");
            character2.getSettings().setShowCharacter(true);

            given(characterRepository.getCharacterList("test@test.com"))
                    .willReturn(List.of(character1, character2));

            // when
            UpdateDayCheckAllCharactersResponse response =
                    characterService.updateDayCheckAllCharacters("test@test.com", "루페온");

            // then
            assertThat(response.getServerName()).isEqualTo("루페온");
        }
    }

    @Nested
    @DisplayName("updateWeekSilmael 메서드")
    class UpdateWeekSilmaelTest {

        @Test
        @DisplayName("성공 - 실마엘 교환 업데이트")
        void success() {
            // given
            WeekTodo weekTodo = new WeekTodo();
            testCharacter.setWeekTodo(weekTodo);

            // when
            characterService.updateWeekSilmael(testCharacter);

            // then
            assertThat(testCharacter.getWeekTodo().isSilmaelChange()).isTrue();
        }
    }

    @Nested
    @DisplayName("updateCubeTicket 메서드")
    class UpdateCubeTicketTest {

        @Test
        @DisplayName("성공 - 큐브 티켓 증가")
        void success_increment() {
            // given
            WeekTodo weekTodo = new WeekTodo();
            testCharacter.setWeekTodo(weekTodo);

            // when
            characterService.updateCubeTicket(testCharacter, 1);

            // then
            assertThat(testCharacter.getWeekTodo().getCubeTicket()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getCharacterList 메서드")
    class GetCharacterListTest {

        @Test
        @DisplayName("성공 - 캐릭터 목록 조회 및 정렬")
        void success() {
            // given
            Character character1 = createTestCharacter();
            character1.setSortNumber(1);
            character1.setItemLevel(1600.0);

            Character character2 = createTestCharacter();
            character2.setId(2L);
            character2.setSortNumber(0);
            character2.setItemLevel(1620.0);

            given(characterRepository.getCharacterList("test@test.com"))
                    .willReturn(List.of(character1, character2));

            // when
            List<CharacterResponse> result = characterService.getCharacterList("test@test.com");

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSortNumber()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("updateMemo 메서드")
    class UpdateMemoTest {

        @Test
        @DisplayName("성공 - 메모 업데이트")
        void success() {
            // when
            characterService.updateMemo(testCharacter, "테스트 메모");

            // then
            assertThat(testCharacter.getMemo()).isEqualTo("테스트 메모");
        }

        @Test
        @DisplayName("성공 - 빈 메모는 null로 저장")
        void success_emptyMemo() {
            // given
            testCharacter.setMemo("기존 메모");

            // when
            characterService.updateMemo(testCharacter, "");

            // then
            assertThat(testCharacter.getMemo()).isNull();
        }
    }

    @Nested
    @DisplayName("deleteByMember 메서드")
    class DeleteByMemberTest {

        @Test
        @DisplayName("성공 - 회원의 모든 캐릭터 삭제")
        void success() {
            // given
            given(characterRepository.deleteByMember(testMember)).willReturn(3L);

            // when
            boolean result = characterService.deleteByMember(testMember);

            // then
            assertThat(result).isTrue();
            verify(characterRepository).deleteByMember(testMember);
        }

        @Test
        @DisplayName("실패 - 삭제할 캐릭터가 없음")
        void fail_noCharacterToDelete() {
            // given
            given(characterRepository.deleteByMember(testMember)).willReturn(0L);

            // when
            boolean result = characterService.deleteByMember(testMember);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("updateGoldCheckVersion 메서드")
    class UpdateGoldCheckVersionTest {

        @Test
        @DisplayName("성공 - 골드 체크 버전 토글")
        void success() {
            // given
            boolean originalVersion = testCharacter.getSettings().isGoldCheckVersion();

            // when
            characterService.updateGoldCheckVersion(testCharacter);

            // then
            assertThat(testCharacter.getSettings().isGoldCheckVersion()).isNotEqualTo(originalVersion);
        }
    }

    @Nested
    @DisplayName("updateWeekEpona 메서드")
    class UpdateWeekEponaTest {

        @Test
        @DisplayName("성공 - 주간 에포나 업데이트")
        void success() {
            // given
            WeekTodo weekTodo = new WeekTodo();
            testCharacter.setWeekTodo(weekTodo);
            UpdateWeekEponaRequest request = new UpdateWeekEponaRequest();
            request.setAllCheck(false);

            // when
            characterService.updateWeekEpona(testCharacter, request);

            // then
            assertThat(testCharacter.getWeekTodo().getWeekEpona()).isEqualTo(1);
        }

        @Test
        @DisplayName("성공 - 주간 에포나 전체 체크")
        void success_allCheck() {
            // given
            WeekTodo weekTodo = new WeekTodo();
            weekTodo.setWeekEpona(0);
            testCharacter.setWeekTodo(weekTodo);
            UpdateWeekEponaRequest request = new UpdateWeekEponaRequest();
            request.setAllCheck(true);

            // when
            characterService.updateWeekEpona(testCharacter, request);

            // then
            assertThat(testCharacter.getWeekTodo().getWeekEpona()).isEqualTo(3);
        }
    }
}
