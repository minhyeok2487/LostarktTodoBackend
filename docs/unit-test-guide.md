# Unit 테스트 작성 가이드

## 개요

이 문서는 Lost Ark Todo Backend 프로젝트의 서비스 레이어 Unit 테스트 작성 과정과 패턴을 설명합니다.

## 테스트 환경

- **JUnit 5** (Jupiter)
- **Mockito** - Mock 객체 생성 및 행위 검증
- **AssertJ** - 가독성 높은 assertion
- **Spring Boot Test** - 테스트 지원

## 테스트 구조

### 디렉토리 구조

```
src/test/java/lostark/todo/domain/
├── character/service/
│   └── CharacterServiceTest.java          # 34개 테스트
├── schedule/service/
│   ├── SchedulingServiceTest.java         # 9개 테스트
│   ├── DayTodoResetServiceTest.java       # 11개 테스트
│   ├── WeekTodoResetServiceTest.java      # 10개 테스트
│   └── ScheduleServiceTest.java           # 23개 테스트
├── member/service/
│   └── MemberServiceTest.java             # 12개 테스트
├── generaltodo/service/
│   └── GeneralTodoServiceTest.java        # 19개 테스트
└── board/community/service/
    └── CommunityServiceTest.java          # 17개 테스트
```

**총 135개 테스트 케이스**

## 테스트 작성 패턴

### 기본 템플릿

```java
@ExtendWith(MockitoExtension.class)
class ServiceNameTest {

    @Mock
    private DependencyRepository repository;

    @Mock
    private OtherService otherService;

    @InjectMocks
    private TargetService targetService;

    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        testEntity = TestEntity.builder()
                .id(1L)
                .name("테스트")
                .build();
    }

    @Nested
    @DisplayName("methodName 메서드")
    class MethodNameTest {

        @Test
        @DisplayName("성공 - 정상 케이스 설명")
        void success() {
            // given
            given(repository.findById(1L)).willReturn(Optional.of(testEntity));

            // when
            Result result = targetService.method(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트");
            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("실패 - 예외 케이스 설명")
        void fail_notFound() {
            // given
            given(repository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> targetService.method(999L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("존재하지 않습니다");
        }
    }
}
```

### 핵심 어노테이션

| 어노테이션 | 용도 |
|-----------|------|
| `@ExtendWith(MockitoExtension.class)` | Mockito 확장 활성화 |
| `@Mock` | Mock 객체 생성 |
| `@InjectMocks` | Mock 주입 대상 서비스 |
| `@Nested` | 메서드별 테스트 그룹화 |
| `@DisplayName` | 한글 테스트 이름 |
| `@BeforeEach` | 테스트 전 공통 설정 |

### BDDMockito 패턴

```java
// Given - 사전 조건 설정
given(repository.find(any())).willReturn(entity);

// When - 실행
Result result = service.method(param);

// Then - 검증
assertThat(result).isNotNull();
verify(repository).find(any());
```

## 서비스별 테스트 상세

### 1. CharacterServiceTest

캐릭터 관련 비즈니스 로직 테스트

**주요 테스트 케이스:**
- `get()` - 캐릭터 조회 및 권한 검증
- `updateGoldCharacter()` - 골드 획득 캐릭터 토글
- `updateRaidGoldCheck()` - 레이드 골드 체크 (3개 제한 검증)
- `updateDayCheck()` - 일일 컨텐츠 체크 (epona/chaos/guardian)
- `validateUpdateDayGauge()` - 휴식 게이지 유효성 검증 (0-100, 10단위)
- `delete()` - 캐릭터 삭제 (골드 캐릭터 삭제 불가)

**테스트 픽스처 예시:**
```java
private Character createTestCharacter() {
    DayContent guardian = DayContent.builder()
            .jewelry(0.5)
            .build();

    DayTodo dayTodo = DayTodo.builder()
            .chaosCheck(0)
            .guardianCheck(0)
            .eponaCheck2(0)
            .guardian(guardian)
            .build();

    return Character.builder()
            .id(1L)
            .characterName("테스트캐릭터")
            .itemLevel(1620.0)
            .goldCharacter(false)
            .dayTodo(dayTodo)
            .weekTodo(new WeekTodo())
            .todoV2List(new ArrayList<>())
            .raidBusGoldList(new ArrayList<>())
            .build();
}
```

### 2. SchedulingServiceTest

스케줄링 작업 테스트 (일일/주간 초기화)

**주요 테스트 케이스:**
- `updateMarketData()` - 거래소 데이터 갱신
- `resetDayTodo()` - 일일 숙제 초기화 (개별 단계 실패 시에도 계속 진행)
- `resetWeekTodo()` - 주간 숙제 초기화
- `checkScheduleRaids()` - 레이드 자동 체크
- `addEnergyToAllLifeEnergies()` - 생활의 기운 추가

**`@Value` 필드 테스트:**
```java
@Test
void success() {
    // @Value 필드는 ReflectionTestUtils로 설정
    ReflectionTestUtils.setField(schedulingService, "apiKey", "test-api-key");

    // given
    given(lostarkMarketApiClient.getMarketData(anyInt(), anyString()))
            .willReturn(marketList);

    // when
    schedulingService.updateMarketData();

    // then
    verify(lostarkMarketApiClient).getMarketData(CategoryCode.재련재료.getValue(), "test-api-key");
}
```

### 3. DayTodoResetServiceTest

일일 숙제 초기화 세부 로직 테스트

**주요 테스트 케이스:**
- `updateDayContentGauge()` - 휴식 게이지 업데이트
- `saveBeforeGauge()` - 이전 게이지 저장
- `updateDayContentCheck()` - 체크 상태 초기화
- `updateDayTodoGold()` - 가디언 토벌 가격 계산 (3티어/4티어 분기)
- `updateCustomDailyTodo()` - 커스텀 일일 숙제 초기화
- `resetServerTodoState()` - 서버 숙제 상태 초기화

### 4. WeekTodoResetServiceTest

주간 숙제 초기화 세부 로직 테스트

**주요 테스트 케이스:**
- `updateTwoCycle()` - 2주기 체크 토글
- `resetTodoV2CoolTime2()` - 2주기 레이드 쿨타임 처리
- `resetTodoV2()` - 주간 레이드 초기화
- `updateWeekContent()` - 주간 숙제 초기화
- `updateWeekDayTodoTotalGold()` - 일일 수익 주간 합계 초기화
- `updateCustomWeeklyTodo()` - 커스텀 주간 숙제 초기화
- `deleteAllRaidBusGold()` - 버스비 전체 삭제

### 5. MemberServiceTest

회원 관리 로직 테스트

**주요 테스트 케이스:**
- `get()` - 회원 조회 (username/id)
- `createCharacter()` - 캐릭터 생성 (락 관리 포함)
- `editMainCharacter()` - 대표 캐릭터 변경
- `editProvider()` - 소셜 → 일반 로그인 전환
- `updatePassword()` - 비밀번호 변경 (이메일 인증 검증)
- `deleteByAdmin()` - 관리자 회원 삭제

**분산 락 테스트:**
```java
@Test
void success() throws Exception {
    // given
    MemberLock mockLock = mock(MemberLock.class);
    given(memberLockManager.acquireLock("test@test.com")).willReturn(mockLock);
    given(memberRepository.get("test@test.com")).willReturn(testMember);

    // when
    memberService.createCharacter("test@test.com", request);

    // then
    verify(memberLockManager).acquireLock("test@test.com");
    verify(mockLock).close(); // 락 해제 검증
}
```

### 6. GeneralTodoServiceTest

범용 할일 관리 테스트 (Folder/Category/Status/Item)

**주요 테스트 케이스:**
- Folder CRUD - 생성/이름변경/순서변경/삭제 + 정렬 순서 shift 로직
- Category CRUD - 생성/수정/순서변경/삭제 + 기본 상태 생성
- Status CRUD - 생성/이름변경/삭제 + 최소 1개 보장
- Item CRUD - 생성/수정/삭제 + TIMELINE 뷰 날짜 검증

**Mockito Strictness 설정:**
```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeneralTodoServiceTest {
    // 사용되지 않는 stub 경고 방지
}
```

### 7. ScheduleServiceTest

일정 관리 테스트 (ALONE/PARTY 일정)

**주요 테스트 케이스:**
- `create()` - ALONE/PARTY 일정 생성, 시간 10분 단위 검증, 깐부 조건 검증
- `edit()` - 리더의 일정 수정, 파티 일정 연쇄 수정
- `remove()` - 리더(전체 삭제)/멤버(본인만 삭제) 분기
- `editFriend()` - 깐부 추가/삭제
- `search()` - ALONE/PARTY 일정 조회, 친구 이름 매핑

**검증 로직 테스트:**
```java
@Test
@DisplayName("실패 - 시간이 10분 단위가 아님")
void fail_timeNotTenMinuteUnit() {
    // given
    CreateScheduleRequest request = createScheduleRequest(
            ScheduleCategory.ALONE,
            LocalTime.of(19, 5),  // 10분 단위 아님
            null
    );

    // when & then
    assertThatThrownBy(() -> scheduleService.create(testCharacter, request))
            .isInstanceOf(ConditionNotMetException.class)
            .hasMessageContaining("10분 단위");
}
```

### 8. CommunityServiceTest

커뮤니티 게시판 테스트

**주요 테스트 케이스:**
- `search()` - 로그인/비로그인 사용자 조회
- `save()` - 게시글/댓글 저장, 이미지 첨부, 알림 생성
- `save()` 검증 - 공지사항 권한, 댓글 구조 오류
- `uploadImage()` - 이미지 업로드 및 저장
- `update()` - 15분 이내 수정 성공, 15분 경과 후 수정 실패
- `delete()` - 게시글 삭제 (soft delete)
- `updateLike()` - 좋아요 추가/취소 토글

**시간 기반 로직 테스트:**
```java
@Test
@DisplayName("실패 - 15분 경과 후 수정 시도")
void fail_updateAfter15Minutes() {
    // given
    Community community = Community.builder().id(1L).body("원래 내용").build();
    Community spyCommunity = spy(community);
    given(spyCommunity.getCreatedDate()).willReturn(LocalDateTime.now().minusMinutes(20));

    given(communityRepository.get("test@test.com", 1L)).willReturn(spyCommunity);

    // when & then
    assertThatThrownBy(() -> communityService.update("test@test.com", request))
            .isInstanceOf(ConditionNotMetException.class)
            .hasMessageContaining("15분이 지나");
}
```

## 테스트 실행

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 서비스 테스트 실행
```bash
./gradlew test --tests "lostark.todo.domain.character.service.CharacterServiceTest"
```

### 특정 메서드 테스트 실행
```bash
./gradlew test --tests "lostark.todo.domain.character.service.CharacterServiceTest\$UpdateDayCheckTest"
```

## 테스트 작성 시 주의사항

### 1. Mock 반환 타입 일치

Repository 메서드의 반환 타입과 Mock 반환값의 타입이 일치해야 합니다.

```java
// Repository가 int를 반환하면
given(repository.updateSomething()).willReturn(100);  // int

// Repository가 long을 반환하면
given(repository.updateSomething()).willReturn(100L); // long
```

### 2. 엔티티 연관관계 초기화

엔티티 생성 시 연관 컬렉션을 초기화해야 NPE를 방지할 수 있습니다.

```java
Character character = Character.builder()
        .todoV2List(new ArrayList<>())      // 초기화 필수
        .raidBusGoldList(new ArrayList<>()) // 초기화 필수
        .build();
```

### 3. 계산 로직에 필요한 의존성

엔티티의 계산 메서드가 내부 객체에 의존하는 경우, 해당 객체도 설정해야 합니다.

```java
// DayTodo.calculateDayTodo()는 guardian.getJewelry()를 사용
DayContent guardian = DayContent.builder()
        .jewelry(0.5)
        .build();

DayTodo dayTodo = DayTodo.builder()
        .guardian(guardian)  // 필수
        .build();
```

### 4. Spy를 활용한 부분 Mocking

엔티티의 특정 메서드만 Mock하고 싶을 때 spy를 사용합니다.

```java
Community spyCommunity = spy(community);
given(spyCommunity.getCreatedDate()).willReturn(LocalDateTime.now().minusMinutes(20));
```

## 커밋 히스토리

```
802322e test: CommunityServiceTest 추가
24eb2b4 test: ScheduleServiceTest 추가
[이전] test: GeneralTodoServiceTest 추가
[이전] test: MemberServiceTest 추가
[이전] test: SchedulingServiceTest 추가
[이전] test: WeekTodoResetServiceTest 추가
[이전] test: DayTodoResetServiceTest 추가
[이전] test: CharacterServiceTest 추가
```
