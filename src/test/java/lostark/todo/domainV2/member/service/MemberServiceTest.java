package lostark.todo.domainV2.member.service;

import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.data.CharacterTestData;
import lostark.todo.data.MarketTestData;
import lostark.todo.data.MemberTestData;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.lostark.dao.LostarkCharacterApiClient;
import lostark.todo.domainV2.member.infra.MemberLockManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static lostark.todo.Constant.TEST_USERNAME;
import static lostark.todo.data.TestConstant.API_KEY;
import static lostark.todo.global.exhandler.ErrorMessageConstants.EMAIL_REGISTRATION_IN_PROGRESS;
import static lostark.todo.global.exhandler.ErrorMessageConstants.TEST_MEMBER_NOT_ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    LostarkCharacterApiClient lostarkCharacterApiClient;

    @Mock
    MarketRepository marketRepository;

    @Spy
    MemberLockManager lockManager = new MemberLockManager();


    @DisplayName("회원가입 후 캐릭터 추가 성공")
    @Test
    void createCharacter_Success() {
        // given
        String username = "testuser@test.com";
        SaveCharacterRequest request = saveCharacterRequest();

        // Mock 객체들 설정
        Member mockMember = MemberTestData.createMockMember(username);
        List<Character> mockCharacterList = CharacterTestData.createMockCharacterList();
        Map<String, Market> mockMarketMap = MarketTestData.createMockMarketMap();

        // Mock 객체 동작 정의
        assertThat(mockMember).isNotNull();
        when(memberRepository.get(username)).thenReturn(Optional.of(mockMember));
        when(lostarkCharacterApiClient.createCharacterList(request.getCharacterName(), API_KEY)).thenReturn(mockCharacterList);
        when(marketRepository.findLevelUpResource()).thenReturn(mockMarketMap);

        // when
        memberService.createCharacter(username, request);

        // then
        verify(memberRepository).get(username);
        assertThat(lockManager.isLocked(username)).isFalse(); // lock이 해제되었는지 확인

        assertThat(mockMember.getApiKey()).isEqualTo(API_KEY);
        assertThat(mockMember.getMainCharacter()).isEqualTo(request.getCharacterName());
        assertThat(mockMember.getCharacters()).hasSize(mockCharacterList.size());
        assertThat(mockMember.getCharacters().get(0).getDayTodo().getChaosGold()).isNotEqualTo(0);
        assertThat(mockMember.getCharacters().get(0).getDayTodo().getGuardianGold()).isNotEqualTo(0);
    }

    @DisplayName("테스트 계정으로 캐릭터 추가 시도시 예외 발생")
    @Test
    void createCharacter_WithTestUsername_ThrowsException() {
        // given
        SaveCharacterRequest request = saveCharacterRequest();

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                memberService.createCharacter(TEST_USERNAME, request));

        assertThat(exception.getMessage())
                .isEqualTo(TEST_MEMBER_NOT_ACCESS);
    }

    @DisplayName("동시에 같은 계정으로 캐릭터 추가 시도시 예외 발생")
    @Test
    void createCharacter_WithConcurrentAccess_ThrowsException() {
        // given
        String username = "user@test.com";
        SaveCharacterRequest request = saveCharacterRequest();

        // 첫 번째 요청으로 lock 설정
        lockManager.setLock(username);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                memberService.createCharacter(username, request));

        assertThat(exception.getMessage())
                .isEqualTo(EMAIL_REGISTRATION_IN_PROGRESS);

        // 추가 검증
        assertThat(lockManager.isLocked(username)).isTrue();
    }

    @DisplayName("알 수 없는 에러일 때, lock이 정상적으로 해제되는지 확인")
    @Test
    void createCharacter_LockIsReleased_AfterException() {
        // given
        String username = "user@test.com";
        SaveCharacterRequest request = saveCharacterRequest();

        when(memberRepository.get(username))
                .thenThrow(new RuntimeException("Unexpected error"));

        // when & then
        assertThrows(RuntimeException.class, () ->
                memberService.createCharacter(username, request));

        assertThat(lockManager.isLocked(username)).isFalse();
    }

    @DisplayName("멀티스레드 환경에서 동시에 같은 계정으로 시도시 한 번만 성공")
    @Test
    void createCharacter_ConcurrentAccess_OnlyOneSucceeds() throws InterruptedException {
        // given
        String username = "user@test.com";
        SaveCharacterRequest request = saveCharacterRequest();

        Member mockMember = MemberTestData.createMockMember(username);
        List<Character> mockCharacters = CharacterTestData.createMockCharacterList();
        Map<String, Market> mockMarketMap = MarketTestData.createMockMarketMap();

        assertThat(mockMember).isNotNull();
        when(memberRepository.get(username)).thenReturn(Optional.of(mockMember));
        when(marketRepository.findLevelUpResource()).thenReturn(mockMarketMap);
        when(lostarkCharacterApiClient.createCharacterList(request.getCharacterName(), API_KEY))
                .thenReturn(mockCharacters);

        // when
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    memberService.createCharacter(username, request);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // then
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue(); // 타임아웃 없이 모든 스레드가 완료되었는지 확인

        assertThat(successCount.get()).isEqualTo(1); // 성공 한번
        assertThat(failCount.get()).isEqualTo(threadCount - 1); // 나머지 다 실패
        assertThat(lockManager.isLocked(username)).isFalse();
    }

    private SaveCharacterRequest saveCharacterRequest() {
        return SaveCharacterRequest.builder()
                .apiKey(API_KEY)
                .characterName("마볼링")
                .build();
    }
}