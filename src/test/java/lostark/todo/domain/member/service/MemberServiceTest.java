package lostark.todo.domain.member.service;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.entity.WeekTodo;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.character.repository.CharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.market.entity.Market;
import lostark.todo.domain.market.service.MarketService;
import lostark.todo.domain.member.dto.ResetPasswordRequest;
import lostark.todo.domain.member.dto.SaveCharacterRequest;
import lostark.todo.domain.member.entity.AuthMail;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.infra.MemberLockManager;
import lostark.todo.domain.member.infra.MemberLockManager.MemberLock;
import lostark.todo.domain.member.repository.AdsRepository;
import lostark.todo.domain.member.repository.AuthMailRepository;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberLockManager memberLockManager;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private AuthMailRepository authMailRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LostarkCharacterApiClient lostarkCharacterApiClient;

    @Mock
    private MarketService marketService;

    @Mock
    private AdsRepository adsRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .mainCharacter("테스트캐릭터")
                .authProvider("none")
                .characters(new ArrayList<>())
                .build();
    }

    private Character createTestCharacter(String name) {
        DayContent guardian = DayContent.builder()
                .jewelry(0.5)
                .build();

        DayTodo dayTodo = DayTodo.builder()
                .chaosCheck(0)
                .chaosGauge(100)
                .guardianCheck(0)
                .guardianGauge(40)
                .eponaCheck2(0)
                .eponaGauge(60)
                .guardian(guardian)
                .build();

        return Character.builder()
                .id(1L)
                .characterName(name)
                .serverName("루페온")
                .itemLevel(1620.0)
                .characterLevel(70)
                .characterClassName("버서커")
                .combatPower(50000.0)
                .goldCharacter(false)
                .dayTodo(dayTodo)
                .weekTodo(new WeekTodo())
                .todoV2List(new ArrayList<>())
                .raidBusGoldList(new ArrayList<>())
                .settings(new Settings())
                .isDeleted(false)
                .member(testMember)
                .sortNumber(0)
                .build();
    }

    @Nested
    @DisplayName("get(String username) 메서드")
    class GetByUsernameTest {

        @Test
        @DisplayName("성공 - 회원 조회")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            // when
            Member result = memberService.get("test@test.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("test@test.com");
            verify(memberRepository).get("test@test.com");
        }
    }

    @Nested
    @DisplayName("get(Long id) 메서드")
    class GetByIdTest {

        @Test
        @DisplayName("성공 - ID로 회원 조회")
        void success() {
            // given
            given(memberRepository.get(1L)).willReturn(testMember);

            // when
            Member result = memberService.get(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("createCharacter 메서드")
    class CreateCharacterTest {

        @Test
        @DisplayName("성공 - 캐릭터 생성")
        void success() throws Exception {
            // given
            MemberLock mockLock = mock(MemberLock.class);
            given(memberLockManager.acquireLock("test@test.com")).willReturn(mockLock);
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            Character character = createTestCharacter("새캐릭터");
            given(lostarkCharacterApiClient.createCharacterList(anyString(), anyString()))
                    .willReturn(List.of(character));

            Map<String, Market> marketMap = new HashMap<>();
            marketMap.put("3티어 1레벨 보석", Market.builder().recentPrice(100).build());
            marketMap.put("4티어 1레벨 보석", Market.builder().recentPrice(200).build());
            given(marketService.findLevelUpResource()).willReturn(marketMap);

            SaveCharacterRequest request = SaveCharacterRequest.builder()
                    .characterName("새캐릭터")
                    .apiKey("test-api-key")
                    .build();

            // when
            memberService.createCharacter("test@test.com", request);

            // then
            verify(memberLockManager).acquireLock("test@test.com");
            verify(lostarkCharacterApiClient).createCharacterList("새캐릭터", "test-api-key");
            verify(marketService).findLevelUpResource();
            verify(mockLock).close();
        }

        @Test
        @DisplayName("실패 - 이미 캐릭터가 존재함")
        void fail_characterAlreadyExists() throws Exception {
            // given
            MemberLock mockLock = mock(MemberLock.class);
            given(memberLockManager.acquireLock("test@test.com")).willReturn(mockLock);

            testMember.getCharacters().add(createTestCharacter("기존캐릭터"));
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            SaveCharacterRequest request = SaveCharacterRequest.builder()
                    .characterName("새캐릭터")
                    .apiKey("test-api-key")
                    .build();

            // when & then
            assertThatThrownBy(() -> memberService.createCharacter("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class);

            verify(mockLock).close();
        }
    }

    @Nested
    @DisplayName("editMainCharacter 메서드")
    class EditMainCharacterTest {

        @Test
        @DisplayName("성공 - 대표 캐릭터 변경")
        void success() {
            // given
            Character character = createTestCharacter("새대표캐릭터");
            testMember.getCharacters().add(character);
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            // when
            memberService.editMainCharacter("test@test.com", "새대표캐릭터");

            // then
            assertThat(testMember.getMainCharacter()).isEqualTo("새대표캐릭터");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 캐릭터")
        void fail_characterNotFound() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            // when & then
            assertThatThrownBy(() ->
                    memberService.editMainCharacter("test@test.com", "없는캐릭터"))
                    .isInstanceOf(ConditionNotMetException.class);
        }
    }

    @Nested
    @DisplayName("editProvider 메서드")
    class EditProviderTest {

        @Test
        @DisplayName("성공 - 소셜에서 일반 로그인으로 전환")
        void success() {
            // given
            testMember = Member.builder()
                    .id(1L)
                    .username("test@test.com")
                    .authProvider("google")
                    .characters(new ArrayList<>())
                    .build();
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(passwordEncoder.encode("newPassword")).willReturn("encodedPassword");

            // when
            memberService.editProvider("test@test.com", "newPassword");

            // then
            assertThat(testMember.getAuthProvider()).isEqualTo("none");
            verify(passwordEncoder).encode("newPassword");
        }

        @Test
        @DisplayName("실패 - 이미 일반 회원인 경우")
        void fail_alreadyNormalMember() {
            // given
            testMember = Member.builder()
                    .id(1L)
                    .username("test@test.com")
                    .authProvider("none")
                    .characters(new ArrayList<>())
                    .build();
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            // when & then
            assertThatThrownBy(() ->
                    memberService.editProvider("test@test.com", "newPassword"))
                    .isInstanceOf(ConditionNotMetException.class);
        }
    }

    @Nested
    @DisplayName("updatePassword 메서드")
    class UpdatePasswordTest {

        @Test
        @DisplayName("성공 - 비밀번호 변경")
        void success() {
            // given
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .mail("test@test.com")
                    .number(123456)
                    .newPassword("newPassword123")
                    .build();

            AuthMail authMail = AuthMail.builder()
                    .mail("test@test.com")
                    .number(123456)
                    .build();

            given(authMailRepository.getAuthMail("test@test.com", 123456))
                    .willReturn(Optional.of(authMail));
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(passwordEncoder.encode("newPassword123")).willReturn("encodedPassword");

            // when
            memberService.updatePassword(request);

            // then
            verify(passwordEncoder).encode("newPassword123");
            verify(authMailRepository).deleteAllByMail("test@test.com");
        }

        @Test
        @DisplayName("실패 - 이메일 인증 실패")
        void fail_authMailNotFound() {
            // given
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .mail("test@test.com")
                    .number(999999)
                    .newPassword("newPassword123")
                    .build();

            given(authMailRepository.getAuthMail("test@test.com", 999999))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.updatePassword(request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("이메일 인증이 실패하였습니다");
        }
    }

    @Nested
    @DisplayName("editApiKey 메서드")
    class EditApiKeyTest {

        @Test
        @DisplayName("성공 - API 키 수정")
        void success() {
            // when
            memberService.editApiKey(testMember, "new-api-key");

            // then
            assertThat(testMember.getApiKey()).isEqualTo("new-api-key");
        }
    }

    @Nested
    @DisplayName("deleteByAdmin 메서드")
    class DeleteByAdminTest {

        @Test
        @DisplayName("성공 - 회원 삭제")
        void success() {
            // given
            given(memberRepository.get(1L)).willReturn(testMember);

            // when
            memberService.deleteByAdmin(1L);

            // then
            verify(memberRepository).delete(testMember);
        }
    }
}
