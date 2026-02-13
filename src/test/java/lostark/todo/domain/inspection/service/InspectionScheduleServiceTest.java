// package lostark.todo.domain.inspection.service;
// 
// import lostark.todo.domain.inspection.entity.InspectionCharacter;
// import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
// import lostark.todo.domain.member.entity.Member;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// 
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// 
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.*;
// 
// @ExtendWith(MockitoExtension.class)
// @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
// class InspectionScheduleServiceTest {
// 
//     @Mock
//     private InspectionCharacterRepository inspectionCharacterRepository;
// 
//     @Mock
//     private InspectionService inspectionService;
// 
//     @InjectMocks
//     private InspectionScheduleService inspectionScheduleService;
// 
//     private Member testMember;
//     private InspectionCharacter testCharacter1;
//     private InspectionCharacter testCharacter2;
// 
//     @BeforeEach
//     void setUp() {
//         testMember = Member.builder()
//                 .id(1L)
//                 .username("test@test.com")
//                 .apiKey("test-api-key")
//                 .inspectionScheduleHour(7)
//                 .build();
// 
//         testCharacter1 = InspectionCharacter.builder()
//                 .id(1L)
//                 .member(testMember)
//                 .characterName("캐릭터1")
//                 .isActive(true)
//                 .combatPower(2200.0)
//                 .histories(new ArrayList<>())
//                 .build();
// 
//         testCharacter2 = InspectionCharacter.builder()
//                 .id(2L)
//                 .member(testMember)
//                 .characterName("캐릭터2")
//                 .isActive(true)
//                 .combatPower(2100.0)
//                 .histories(new ArrayList<>())
//                 .build();
//     }
// 
//     @Nested
//     @DisplayName("fetchScheduledInspectionData 메서드")
//     class FetchScheduledInspectionDataTest {
// 
//         @Test
//         @DisplayName("성공 - 수집 대상이 없으면 조기 반환")
//         void success_noTargets() {
//             // given
//             given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
//                     .willReturn(Collections.emptyList());
// 
//             // when
//             inspectionScheduleService.fetchScheduledInspectionData();
// 
//             // then
//             verify(inspectionService, never()).fetchDailyData(any(), anyString());
//         }
// 
//         @Test
//         @DisplayName("성공 - 여러 캐릭터 데이터 수집")
//         void success_multipleCharacters() {
//             // given
//             given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
//                     .willReturn(List.of(testCharacter1, testCharacter2));
// 
//             // when
//             inspectionScheduleService.fetchScheduledInspectionData();
// 
//             // then
//             verify(inspectionService).fetchDailyData(testCharacter1, "test-api-key");
//             verify(inspectionService).fetchDailyData(testCharacter2, "test-api-key");
//         }
// 
//         @Test
//         @DisplayName("성공 - API 키 없는 캐릭터 스킵")
//         void success_skipNoApiKey() {
//             // given
//             Member noKeyMember = Member.builder()
//                     .id(2L)
//                     .username("nokey@test.com")
//                     .build();
//             InspectionCharacter noKeyCharacter = InspectionCharacter.builder()
//                     .id(3L)
//                     .member(noKeyMember)
//                     .characterName("키없는캐릭터")
//                     .isActive(true)
//                     .histories(new ArrayList<>())
//                     .build();
// 
//             given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
//                     .willReturn(List.of(testCharacter1, noKeyCharacter));
// 
//             // when
//             inspectionScheduleService.fetchScheduledInspectionData();
// 
//             // then
//             verify(inspectionService).fetchDailyData(testCharacter1, "test-api-key");
//             verify(inspectionService, never()).fetchDailyData(eq(noKeyCharacter), anyString());
//         }
// 
//         @Test
//         @DisplayName("성공 - 일부 캐릭터 수집 실패해도 나머지 계속 수집")
//         void success_continueOnFailure() {
//             // given
//             given(inspectionCharacterRepository.findActiveByScheduleHour(anyInt()))
//                     .willReturn(List.of(testCharacter1, testCharacter2));
//             doThrow(new RuntimeException("API 실패"))
//                     .when(inspectionService).fetchDailyData(testCharacter1, "test-api-key");
// 
//             // when
//             inspectionScheduleService.fetchScheduledInspectionData();
// 
//             // then - 첫 번째 실패해도 두 번째는 수집됨
//             verify(inspectionService).fetchDailyData(testCharacter1, "test-api-key");
//             verify(inspectionService).fetchDailyData(testCharacter2, "test-api-key");
//         }
//     }
// }
