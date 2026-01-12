package lostark.todo.domain.schedule.service;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.schedule.dto.*;
import lostark.todo.domain.schedule.entity.Schedule;
import lostark.todo.domain.schedule.enums.ScheduleCategory;
import lostark.todo.domain.schedule.enums.ScheduleRaidCategory;
import lostark.todo.domain.schedule.repository.ScheduleRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Character testCharacter;

    @BeforeEach
    void setUp() {
        testCharacter = Character.builder()
                .id(1L)
                .characterName("테스트캐릭터")
                .build();
    }

    private CreateScheduleRequest createScheduleRequest(ScheduleCategory category, LocalTime time, List<Long> friendIds) {
        return new CreateScheduleRequest(
                category,
                ScheduleRaidCategory.RAID,
                "발탄",
                "하드",
                DayOfWeek.WEDNESDAY,
                time,
                true,
                LocalDate.now(),
                "메모",
                1L,
                friendIds,
                true
        );
    }

    private Schedule createTestSchedule(long id, boolean isLeader, long leaderScheduleId) {
        return Schedule.builder()
                .id(id)
                .characterId(1L)
                .scheduleRaidCategory(ScheduleRaidCategory.RAID)
                .scheduleCategory(ScheduleCategory.PARTY)
                .raidName("발탄")
                .raidLevel("하드")
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .time(LocalTime.of(19, 0))
                .memo("메모")
                .repeatWeek(true)
                .leader(isLeader)
                .leaderScheduleId(leaderScheduleId)
                .build();
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("성공 - ALONE 일정 생성")
        void success_aloneSchedule() {
            // given
            CreateScheduleRequest request = createScheduleRequest(ScheduleCategory.ALONE, LocalTime.of(19, 0), null);
            Schedule savedSchedule = createTestSchedule(1L, true, 0L);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(savedSchedule);

            // when
            scheduleService.create(testCharacter, request);

            // then
            verify(scheduleRepository).save(any(Schedule.class));
            verify(scheduleRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("성공 - PARTY 일정 생성 (깐부 포함)")
        void success_partyScheduleWithFriends() {
            // given
            List<Long> friendIds = List.of(2L, 3L);
            CreateScheduleRequest request = createScheduleRequest(ScheduleCategory.PARTY, LocalTime.of(19, 0), friendIds);
            Schedule savedSchedule = createTestSchedule(1L, true, 0L);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(savedSchedule);

            // when
            scheduleService.create(testCharacter, request);

            // then
            verify(scheduleRepository).save(any(Schedule.class));
            verify(scheduleRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("실패 - 시간이 10분 단위가 아님")
        void fail_timeNotTenMinuteUnit() {
            // given
            CreateScheduleRequest request = createScheduleRequest(ScheduleCategory.ALONE, LocalTime.of(19, 5), null);

            // when & then
            assertThatThrownBy(() -> scheduleService.create(testCharacter, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("10분 단위");
        }

        @Test
        @DisplayName("실패 - PARTY 일정인데 깐부가 없음")
        void fail_partyWithoutFriends() {
            // given
            CreateScheduleRequest request = createScheduleRequest(ScheduleCategory.PARTY, LocalTime.of(19, 0), null);

            // when & then
            assertThatThrownBy(() -> scheduleService.create(testCharacter, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("깐부를 추가");
        }

        @Test
        @DisplayName("실패 - ALONE 일정인데 깐부가 있음")
        void fail_aloneWithFriends() {
            // given
            List<Long> friendIds = List.of(2L);
            CreateScheduleRequest request = createScheduleRequest(ScheduleCategory.ALONE, LocalTime.of(19, 0), friendIds);

            // when & then
            assertThatThrownBy(() -> scheduleService.create(testCharacter, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("깐부가 없어야");
        }
    }

    @Nested
    @DisplayName("getResponseIsReader 메서드")
    class GetResponseIsReaderTest {

        @Test
        @DisplayName("성공 - 일정 조회")
        void success() {
            // given
            GetScheduleResponse response = mock(GetScheduleResponse.class);
            given(scheduleRepository.getResponse(1L, "test@test.com", 0L))
                    .willReturn(Optional.of(response));

            // when
            GetScheduleResponse result = scheduleService.getResponseIsReader(1L, "test@test.com", 0L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("실패 - 일정이 존재하지 않음")
        void fail_notFound() {
            // given
            given(scheduleRepository.getResponse(1L, "test@test.com", 0L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.getResponseIsReader(1L, "test@test.com", 0L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("없는 일정");
        }
    }

    @Nested
    @DisplayName("getLeaderScheduleId 메서드")
    class GetLeaderScheduleIdTest {

        @Test
        @DisplayName("성공 - 리더 일정 ID로 조회")
        void success() {
            // given
            List<ScheduleCharacterResponse> responses = List.of(mock(ScheduleCharacterResponse.class));
            given(scheduleRepository.getLeaderScheduleId(1L)).willReturn(responses);

            // when
            List<ScheduleCharacterResponse> result = scheduleService.getLeaderScheduleId(1L);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("edit 메서드")
    class EditTest {

        @Test
        @DisplayName("성공 - 리더가 ALONE 일정 수정")
        void success_leaderEditAloneSchedule() {
            // given
            Schedule schedule = Schedule.builder()
                    .id(1L)
                    .characterId(1L)
                    .scheduleCategory(ScheduleCategory.ALONE)
                    .leader(true)
                    .build();
            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(schedule));

            EditScheduleRequest request = new EditScheduleRequest(
                    LocalDate.now(), DayOfWeek.FRIDAY, LocalTime.of(20, 0), "수정된 메모", true
            );

            // when
            scheduleService.edit("test@test.com", request, 1L);

            // then
            assertThat(schedule.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
            assertThat(schedule.getTime()).isEqualTo(LocalTime.of(20, 0));
            assertThat(schedule.getMemo()).isEqualTo("수정된 메모");
        }

        @Test
        @DisplayName("성공 - 리더가 PARTY 일정 수정 (깐부 일정도 함께 수정)")
        void success_leaderEditPartySchedule() {
            // given
            Schedule leaderSchedule = Schedule.builder()
                    .id(1L)
                    .characterId(1L)
                    .scheduleCategory(ScheduleCategory.PARTY)
                    .leader(true)
                    .build();
            Schedule friendSchedule1 = Schedule.builder()
                    .id(2L)
                    .characterId(2L)
                    .scheduleCategory(ScheduleCategory.PARTY)
                    .leader(false)
                    .leaderScheduleId(1L)
                    .build();
            Schedule friendSchedule2 = Schedule.builder()
                    .id(3L)
                    .characterId(3L)
                    .scheduleCategory(ScheduleCategory.PARTY)
                    .leader(false)
                    .leaderScheduleId(1L)
                    .build();

            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(leaderSchedule));
            given(scheduleRepository.searchFriend(1L)).willReturn(List.of(friendSchedule1, friendSchedule2));

            EditScheduleRequest request = new EditScheduleRequest(
                    LocalDate.now(), DayOfWeek.SATURDAY, LocalTime.of(21, 0), "파티 메모", false
            );

            // when
            scheduleService.edit("test@test.com", request, 1L);

            // then
            assertThat(leaderSchedule.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
            assertThat(friendSchedule1.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
            assertThat(friendSchedule2.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
        }

        @Test
        @DisplayName("실패 - 파티장이 아닌 경우 수정 불가")
        void fail_notLeader() {
            // given
            Schedule schedule = Schedule.builder()
                    .id(2L)
                    .characterId(2L)
                    .scheduleCategory(ScheduleCategory.PARTY)
                    .leader(false)
                    .leaderScheduleId(1L)
                    .build();
            given(scheduleRepository.get(2L, "test@test.com")).willReturn(Optional.of(schedule));

            EditScheduleRequest request = new EditScheduleRequest(
                    LocalDate.now(), DayOfWeek.FRIDAY, LocalTime.of(20, 0), "수정 메모", true
            );

            // when & then
            assertThatThrownBy(() -> scheduleService.edit("test@test.com", request, 2L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("파티장만 수정");
        }

        @Test
        @DisplayName("실패 - 일정이 존재하지 않음")
        void fail_notFound() {
            // given
            given(scheduleRepository.get(anyLong(), any())).willReturn(Optional.empty());

            EditScheduleRequest request = new EditScheduleRequest(
                    LocalDate.now(), DayOfWeek.FRIDAY, LocalTime.of(20, 0), "메모", true
            );

            // when & then
            assertThatThrownBy(() -> scheduleService.edit("test@test.com", request, 999L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("없는 일정");
        }
    }

    @Nested
    @DisplayName("remove 메서드")
    class RemoveTest {

        @Test
        @DisplayName("성공 - 리더가 일정 삭제 (전체 삭제)")
        void success_leaderRemove() {
            // given
            Schedule schedule = createTestSchedule(1L, true, 0L);
            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(schedule));

            // when
            scheduleService.remove("test@test.com", 1L);

            // then
            verify(scheduleRepository).remove(1L);
            verify(scheduleRepository, never()).delete(any(Schedule.class));
        }

        @Test
        @DisplayName("성공 - 멤버가 일정 삭제 (본인만 삭제)")
        void success_memberRemove() {
            // given
            Schedule schedule = createTestSchedule(2L, false, 1L);
            given(scheduleRepository.get(2L, "test@test.com")).willReturn(Optional.of(schedule));

            // when
            scheduleService.remove("test@test.com", 2L);

            // then
            verify(scheduleRepository).delete(schedule);
            verify(scheduleRepository, never()).remove(anyLong());
        }

        @Test
        @DisplayName("실패 - 일정이 존재하지 않음")
        void fail_notFound() {
            // given
            given(scheduleRepository.get(anyLong(), any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.remove("test@test.com", 999L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("없는 일정");
        }
    }

    @Nested
    @DisplayName("editFriend 메서드")
    class EditFriendTest {

        @Test
        @DisplayName("성공 - 깐부 추가")
        void success_addFriends() {
            // given
            Schedule leaderSchedule = createTestSchedule(1L, true, 0L);
            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(leaderSchedule));
            given(scheduleRepository.searchFriend(1L)).willReturn(new ArrayList<>());
            given(scheduleRepository.save(any(Schedule.class))).willAnswer(invocation -> invocation.getArgument(0));

            EditScheduleFriendRequest request = new EditScheduleFriendRequest(null, List.of(4L, 5L));

            // when
            scheduleService.editFriend("test@test.com", request, 1L);

            // then
            verify(scheduleRepository, times(2)).save(any(Schedule.class));
        }

        @Test
        @DisplayName("성공 - 깐부 삭제")
        void success_removeFriends() {
            // given
            Schedule leaderSchedule = createTestSchedule(1L, true, 0L);
            Schedule friendSchedule1 = createTestSchedule(2L, false, 1L);
            friendSchedule1.setCharacterId(2L);
            Schedule friendSchedule2 = createTestSchedule(3L, false, 1L);
            friendSchedule2.setCharacterId(3L);

            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(leaderSchedule));
            given(scheduleRepository.searchFriend(1L)).willReturn(List.of(friendSchedule1, friendSchedule2));

            EditScheduleFriendRequest request = new EditScheduleFriendRequest(List.of(2L), null);

            // when
            scheduleService.editFriend("test@test.com", request, 1L);

            // then
            verify(scheduleRepository).delete(friendSchedule1);
            verify(scheduleRepository, never()).delete(friendSchedule2);
        }

        @Test
        @DisplayName("성공 - 깐부 추가 및 삭제 동시")
        void success_addAndRemoveFriends() {
            // given
            Schedule leaderSchedule = createTestSchedule(1L, true, 0L);
            Schedule friendSchedule = createTestSchedule(2L, false, 1L);
            friendSchedule.setCharacterId(2L);

            given(scheduleRepository.get(1L, "test@test.com")).willReturn(Optional.of(leaderSchedule));
            given(scheduleRepository.searchFriend(1L)).willReturn(List.of(friendSchedule));
            given(scheduleRepository.save(any(Schedule.class))).willAnswer(invocation -> invocation.getArgument(0));

            EditScheduleFriendRequest request = new EditScheduleFriendRequest(List.of(2L), List.of(4L));

            // when
            scheduleService.editFriend("test@test.com", request, 1L);

            // then
            verify(scheduleRepository).delete(friendSchedule);
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("실패 - 파티장이 아닌 경우")
        void fail_notLeader() {
            // given
            Schedule memberSchedule = createTestSchedule(2L, false, 1L);
            given(scheduleRepository.get(2L, "test@test.com")).willReturn(Optional.of(memberSchedule));

            EditScheduleFriendRequest request = new EditScheduleFriendRequest(null, List.of(4L));

            // when & then
            assertThatThrownBy(() -> scheduleService.editFriend("test@test.com", request, 2L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("파티장만 수정");
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("성공 - ALONE 일정 조회")
        void success_searchAloneSchedules() {
            // given
            WeekScheduleResponse aloneResponse = mock(WeekScheduleResponse.class);
            given(aloneResponse.getScheduleCategory()).willReturn(ScheduleCategory.ALONE);

            SearchScheduleRequest request = mock(SearchScheduleRequest.class);
            given(scheduleRepository.search("test@test.com", request)).willReturn(List.of(aloneResponse));
            given(scheduleRepository.getFriendNamesByLeaderScheduleIds(Collections.emptyList())).willReturn(Collections.emptyMap());

            // when
            List<WeekScheduleResponse> result = scheduleService.search("test@test.com", request);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("성공 - PARTY 일정 조회 (리더)")
        void success_searchPartySchedulesAsLeader() {
            // given
            WeekScheduleResponse partyResponse = mock(WeekScheduleResponse.class);
            given(partyResponse.getScheduleCategory()).willReturn(ScheduleCategory.PARTY);
            given(partyResponse.getIsLeader()).willReturn(true);
            given(partyResponse.getScheduleId()).willReturn(1L);

            SearchScheduleRequest request = mock(SearchScheduleRequest.class);
            given(scheduleRepository.search("test@test.com", request)).willReturn(List.of(partyResponse));

            Map<Long, List<String>> friendNamesMap = new HashMap<>();
            friendNamesMap.put(1L, List.of("깐부1", "깐부2"));
            given(scheduleRepository.getFriendNamesByLeaderScheduleIds(List.of(1L))).willReturn(friendNamesMap);

            // when
            List<WeekScheduleResponse> result = scheduleService.search("test@test.com", request);

            // then
            assertThat(result).hasSize(1);
            verify(partyResponse).setFriendCharacterNames(List.of("깐부1", "깐부2"));
        }

        @Test
        @DisplayName("성공 - PARTY 일정 조회 (멤버)")
        void success_searchPartySchedulesAsMember() {
            // given
            WeekScheduleResponse partyResponse = mock(WeekScheduleResponse.class);
            given(partyResponse.getScheduleCategory()).willReturn(ScheduleCategory.PARTY);
            given(partyResponse.getIsLeader()).willReturn(false);
            given(partyResponse.getLeaderScheduleId()).willReturn(1L);

            SearchScheduleRequest request = mock(SearchScheduleRequest.class);
            given(scheduleRepository.search("test@test.com", request)).willReturn(List.of(partyResponse));

            Map<Long, List<String>> friendNamesMap = new HashMap<>();
            friendNamesMap.put(1L, List.of("리더", "깐부1"));
            given(scheduleRepository.getFriendNamesByLeaderScheduleIds(List.of(1L))).willReturn(friendNamesMap);

            // when
            List<WeekScheduleResponse> result = scheduleService.search("test@test.com", request);

            // then
            assertThat(result).hasSize(1);
            verify(partyResponse).setFriendCharacterNames(List.of("리더", "깐부1"));
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void success_emptyResult() {
            // given
            SearchScheduleRequest request = mock(SearchScheduleRequest.class);
            given(scheduleRepository.search("test@test.com", request)).willReturn(Collections.emptyList());

            // when
            List<WeekScheduleResponse> result = scheduleService.search("test@test.com", request);

            // then
            assertThat(result).isEmpty();
        }
    }
}
