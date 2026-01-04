# JPA N+1 문제 해결과 쿼리 카운팅 기반 성능 테스트 인프라 구축

<br>

## 개요

이번 작업에서는 `ScheduleService.search()` 메서드에서 발생하던 N+1 문제를 해결하고, 향후 동일한 문제의 재발을 방지하기 위한 쿼리 카운팅 기반 성능 테스트 인프라를 구축했습니다. 본 글에서는 문제의 원인 분석, 해결 과정, 그리고 테스트 인프라의 설계 의도를 상세히 다룹니다.

<br><br>

## 1. 문제 분석

### 1.1 N+1 문제란

N+1 문제는 ORM을 사용하는 애플리케이션에서 빈번하게 발생하는 성능 이슈입니다. 하나의 쿼리로 N개의 엔티티를 조회한 후, 각 엔티티의 연관 데이터를 가져오기 위해 N번의 추가 쿼리가 발생하는 현상을 말합니다.

예를 들어 100개의 일정을 조회하고, 각 일정에 참여한 친구 목록을 가져와야 한다면:
- 일정 목록 조회: 1회
- 각 일정별 친구 조회: 100회
- **총 101회의 쿼리 발생**

이 문제가 심각한 이유는 데이터 증가에 따라 쿼리 수가 선형적으로 증가하기 때문입니다. 네트워크 왕복 시간(RTT)과 DB 커넥션 점유 시간이 누적되어 응답 시간이 급격히 늘어납니다.
<br>

### 1.2 기존 코드의 문제점

```java
// ScheduleService.java (Before)
@Transactional
public List<WeekScheduleResponse> search(String username, SearchScheduleRequest request) {
    return scheduleRepository.search(username, request).stream()
            .peek(response -> {
                if (response.getScheduleCategory().equals(ScheduleCategory.PARTY)) {
                    if (response.getIsLeader()) {
                        response.setFriendCharacterNames(
                                scheduleRepository.getLeaderScheduleId(response.getScheduleId())
                                        .stream()
                                        .map(ScheduleCharacterResponse::getCharacterName)
                                        .toList());
                    } else {
                        response.setFriendCharacterNames(
                                scheduleRepository.getLeaderScheduleId(response.getLeaderScheduleId())
                                        .stream()
                                        .map(ScheduleCharacterResponse::getCharacterName)
                                        .toList());
                    }
                }
            })
            .toList();
}
```

위 코드의 문제점을 분석하면:

1. **Stream의 `peek()` 내부에서 Repository 호출**: `peek()`은 중간 연산으로, 각 요소를 순회하며 부수 효과를 발생시킵니다. 이 안에서 DB 호출이 발생하면 요소 개수만큼 쿼리가 실행됩니다.

2. **조건부 쿼리 실행**: `PARTY` 카테고리인 경우에만 추가 쿼리가 발생하지만, 파티 일정이 많을수록 문제가 심화됩니다.

3. **트랜잭션 범위**: `@Transactional`이 걸려 있어 전체 작업이 하나의 트랜잭션 내에서 수행되지만, 쿼리 수 자체를 줄이지는 못합니다.

<br><br>

## 2. 해결 전략

### 2.1 접근 방식 선택

N+1 문제를 해결하는 일반적인 방법은 다음과 같습니다:

| 방법 | 장점 | 단점 |
|------|------|------|
| Fetch Join | JPA 표준, 한 번의 쿼리로 해결 | 카테시안 곱 문제, 페이징 불가 |
| @EntityGraph | 선언적, 동적 페치 가능 | 복잡한 조건에서 유연성 부족 |
| @BatchSize | 설정만으로 적용 가능 | 전역 설정, 세밀한 제어 어려움 |
| **별도 쿼리 + 메모리 매핑** | 유연함, 쿼리 수 예측 가능 | 구현 복잡도 증가 |

이번 케이스에서는 **별도 쿼리 + 메모리 매핑** 방식을 선택했습니다. 이유는 다음과 같습니다:

1. 기존 `search()` 메서드가 이미 DTO 프로젝션을 사용하고 있어 Fetch Join 적용이 어렵습니다.
2. 조건부로 연관 데이터가 필요한 상황(PARTY 카테고리만)이라 일괄 Fetch가 비효율적일 수 있습니다.
3. 쿼리 수를 정확히 2개로 고정할 수 있어 성능 예측이 용이합니다.

<br>

### 2.2 개선된 코드

```java
// ScheduleService.java (After)
/**
 * 일정 조회 (N+1 문제 개선 버전)
 * - 기존: PARTY 일정마다 친구 조회 쿼리 발생 (N+1)
 * - 개선: IN 쿼리로 한 번에 조회 후 메모리에서 매핑
 */
@Transactional(readOnly = true)
public List<WeekScheduleResponse> search(String username, SearchScheduleRequest request) {
    List<WeekScheduleResponse> responses = scheduleRepository.search(username, request);

    // Step 1: PARTY 일정들의 leaderScheduleId 수집
    List<Long> leaderScheduleIds = responses.stream()
            .filter(r -> r.getScheduleCategory().equals(ScheduleCategory.PARTY))
            .map(r -> r.getIsLeader() ? r.getScheduleId() : r.getLeaderScheduleId())
            .distinct()
            .toList();

    // Step 2: 한 번의 IN 쿼리로 모든 친구 캐릭터 이름 조회
    Map<Long, List<String>> friendNamesMap =
            scheduleRepository.getFriendNamesByLeaderScheduleIds(leaderScheduleIds);

    // Step 3: 메모리에서 매핑
    responses.stream()
            .filter(r -> r.getScheduleCategory().equals(ScheduleCategory.PARTY))
            .forEach(r -> {
                long key = r.getIsLeader() ? r.getScheduleId() : r.getLeaderScheduleId();
                r.setFriendCharacterNames(friendNamesMap.getOrDefault(key, Collections.emptyList()));
            });

    return responses;
}
```

<br>

### 2.3 Repository 구현

```java
// ScheduleRepositoryImpl.java
@Override
public Map<Long, List<String>> getFriendNamesByLeaderScheduleIds(List<Long> leaderScheduleIds) {
    if (leaderScheduleIds == null || leaderScheduleIds.isEmpty()) {
        return new HashMap<>();
    }

    // IN 쿼리로 한 번에 조회
    List<Tuple> results = factory
            .select(schedule.leaderScheduleId, character.characterName)
            .from(schedule)
            .leftJoin(character).on(schedule.characterId.eq(character.id))
            .where(schedule.leaderScheduleId.in(leaderScheduleIds))
            .fetch();

    // Java에서 그룹화
    Map<Long, List<String>> resultMap = new HashMap<>();
    for (Tuple tuple : results) {
        Long leaderScheduleId = tuple.get(schedule.leaderScheduleId);
        String characterName = tuple.get(character.characterName);
        resultMap.computeIfAbsent(leaderScheduleId, k -> new ArrayList<>()).add(characterName);
    }

    return resultMap;
}
```

<br>

**설계 포인트:**

1. **Early Return 패턴**: 빈 리스트가 들어오면 빈 Map을 즉시 반환하여 불필요한 쿼리를 방지합니다.

2. **Tuple 사용**: 엔티티 전체가 아닌 필요한 필드만 조회하여 메모리 사용을 최소화합니다.

3. **`computeIfAbsent()` 활용**: Java 8+ 스타일로 그룹화 로직을 간결하게 표현했습니다. `Collectors.groupingBy()`를 사용할 수도 있지만, Tuple에서 null 처리가 필요할 수 있어 명시적인 반복문을 선택했습니다.

<br>

### 2.4 변경 전후 쿼리 비교

**Before (파티 일정 10개 기준):**
```sql
-- 1. 일정 목록 조회
SELECT ... FROM schedule WHERE ...

-- 2~11. 각 파티별 친구 조회 (10회 반복)
SELECT ... FROM schedule s JOIN character c ON ... WHERE s.leader_schedule_id = ?
SELECT ... FROM schedule s JOIN character c ON ... WHERE s.leader_schedule_id = ?
...
```

**After:**
```sql
-- 1. 일정 목록 조회
SELECT ... FROM schedule WHERE ...

-- 2. 모든 파티의 친구 한 번에 조회
SELECT s.leader_schedule_id, c.character_name
FROM schedule s
LEFT JOIN character c ON s.character_id = c.id
WHERE s.leader_schedule_id IN (?, ?, ?, ...)
```

<br><br>

## 3. 성능 테스트 인프라 구축

문제를 해결하는 것만으로는 충분하지 않습니다. 코드베이스가 성장하면서 동일한 문제가 재발할 수 있기 때문입니다. 이를 방지하기 위해 쿼리 카운팅 기반 성능 테스트 인프라를 구축했습니다.

### 3.1 설계 목표

1. **테스트 코드에서 쿼리 수 제한**: 특정 테스트가 허용된 쿼리 수를 초과하면 실패하도록 합니다.
2. **비침투적(non-invasive)**: 프로덕션 코드를 수정하지 않고 테스트 환경에서만 동작합니다.
3. **사용 편의성**: 어노테이션 하나로 적용 가능하도록 합니다.

<br>

### 3.2 구성 요소

<br>

#### 3.2.1 QueryCounter

```java
/**
 * SQL 쿼리 횟수를 카운트하는 유틸 클래스
 * ThreadLocal 대신 AtomicInteger 사용 - 테스트 환경에서 단일 스레드 가정
 */
public class QueryCounter {

    private static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static void increment() {
        count.incrementAndGet();
    }

    public static int getCount() {
        return count.get();
    }
}
```

`AtomicInteger`를 사용한 이유는 테스트 환경에서의 스레드 안전성을 보장하면서도 `ThreadLocal`보다 구현이 간단하기 때문입니다. 실제 멀티스레드 테스트가 필요한 경우 `ThreadLocal<AtomicInteger>`로 변경할 수 있습니다.

#### 3.2.2 DataSourceProxyConfig

```java
@TestConfiguration
public class DataSourceProxyConfig {

    @Bean
    public BeanPostProcessor dataSourceProxyBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource && !(bean.getClass().getName().contains("Proxy"))) {
                    return ProxyDataSourceBuilder.create((DataSource) bean)
                            .name("QueryCountingDataSource")
                            .listener(new QueryExecutionListener() {
                                @Override
                                public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                                }

                                @Override
                                public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                                    QueryCounter.increment();
                                }
                            })
                            .build();
                }
                return bean;
            }
        };
    }
}
```

**핵심 설계:**

1. **BeanPostProcessor 활용**: 스프링 컨테이너가 빈을 생성한 직후 DataSource를 프록시로 감쌉니다. 이 방식은 기존 DataSource 설정을 건드리지 않으면서 쿼리 가로채기가 가능합니다.

2. **datasource-proxy 라이브러리**: `net.ttddyy:datasource-proxy`는 JDBC 레벨에서 쿼리를 가로채는 검증된 라이브러리입니다. Hibernate Statistics보다 더 정확한 쿼리 카운팅이 가능합니다.

3. **@TestConfiguration**: 테스트 환경에서만 활성화되어 프로덕션에 영향을 주지 않습니다.

4. **이중 프록시 방지**: `bean.getClass().getName().contains("Proxy")` 체크로 이미 프록시된 DataSource를 다시 감싸지 않도록 합니다.

#### 3.2.3 MeasurePerformance 어노테이션

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PerformanceExtension.class)
public @interface MeasurePerformance {
    int maxQueries() default Integer.MAX_VALUE;
}
```

`@ExtendWith`를 메타 어노테이션으로 포함시켜 사용자가 별도로 Extension을 등록할 필요가 없도록 했습니다.

#### 3.2.4 PerformanceExtension

```java
@Slf4j
public class PerformanceExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String START_TIME = "startTime";
    private static final String MEMORY_BEFORE = "memoryBefore";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        QueryCounter.reset();

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();  // 메모리 측정 정확도를 위한 GC 호출

        ExtensionContext.Store store = getStore(context);
        store.put(START_TIME, System.currentTimeMillis());
        store.put(MEMORY_BEFORE, runtime.totalMemory() - runtime.freeMemory());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ExtensionContext.Store store = getStore(context);
        long startTime = store.remove(START_TIME, Long.class);
        long memoryBefore = store.remove(MEMORY_BEFORE, Long.class);

        long duration = System.currentTimeMillis() - startTime;
        Runtime runtime = Runtime.getRuntime();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (memoryAfter - memoryBefore) / 1024;
        int queryCount = QueryCounter.getCount();

        log.info("실행 시간: {}ms | 메모리: {}KB | 쿼리 수: {}", duration, memoryUsed, queryCount);

        // maxQueries 검증
        context.getTestMethod().ifPresent(method -> {
            MeasurePerformance annotation = method.getAnnotation(MeasurePerformance.class);
            if (annotation != null && annotation.maxQueries() < Integer.MAX_VALUE) {
                assertThat(queryCount)
                        .as("쿼리 수가 %d개를 초과했습니다", annotation.maxQueries())
                        .isLessThanOrEqualTo(annotation.maxQueries());
            }
        });
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
```

**JUnit 5 Extension Model 활용:**

1. **`BeforeTestExecutionCallback` / `AfterTestExecutionCallback`**: 테스트 메서드 실행 직전/직후에 호출됩니다. `@BeforeEach`/`@AfterEach`보다 테스트 메서드에 더 가깝게 측정할 수 있습니다.

2. **`ExtensionContext.Store`**: JUnit 5에서 제공하는 테스트별 저장소로, 테스트 간 데이터 격리를 보장합니다.

3. **조건부 검증**: `maxQueries`가 명시적으로 설정된 경우에만 검증하여, 단순히 성능 로깅만 원하는 경우에도 사용할 수 있습니다.
   <br>

### 3.3 사용 예시

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class ScheduleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("월별 일정 조회 - N+1 문제 해결 검증")
    @MeasurePerformance(maxQueries = 3)
    void search_noNPlusOne() throws Exception {
        String token = tokenProvider.createToken(TEST_USERNAME);

        mockMvc.perform(get("/api/v1/schedule")
                        .header("Authorization", "Bearer " + token)
                        .param("year", "2026")
                        .param("month", "1"))
                .andExpect(status().isOk());
    }
}
```

`maxQueries = 3`으로 설정한 이유:
- 토큰 검증을 위한 Member 조회: 1회
- 일정 목록 조회: 1회
- 친구 캐릭터 이름 조회: 1회
- **합계: 3회**

만약 N+1 문제가 재발하여 4개 이상의 쿼리가 실행되면 테스트가 실패합니다.

<br><br>

## 4. 추가 개선 사항

### 4.1 @Transactional(readOnly = true) 적용

기존 `@Transactional`에서 `@Transactional(readOnly = true)`로 변경했습니다. 이 변경의 의미:

1. **Flush 모드 변경**: Hibernate가 더티 체킹을 수행하지 않아 약간의 성능 향상이 있습니다.
2. **의도 명확화**: 이 메서드가 데이터를 변경하지 않음을 명시적으로 표현합니다.
3. **DB 최적화**: 일부 DB에서는 읽기 전용 힌트를 활용하여 최적화를 수행합니다.

<br><br>

## 5. 성능 비교

| 지표 | Before | After |
|------|--------|-------|
| 쿼리 수 (파티 10개) | 11회 | 2회 |
| 쿼리 수 (파티 100개) | 101회 | 2회 |
| 시간 복잡도 | O(n) | O(1) |

쿼리 수가 데이터 양과 무관하게 상수로 고정되었습니다.

<br><br>

## 6. 결론

이번 작업에서는 단순히 N+1 문제를 해결하는 것을 넘어, 문제의 재발을 방지하기 위한 테스트 인프라까지 구축했습니다. 핵심 내용을 정리하면:

1. **N+1 해결**: IN 쿼리 + 메모리 매핑 패턴으로 쿼리 수를 상수로 고정했습니다.
2. **테스트 인프라**: datasource-proxy + JUnit 5 Extension으로 쿼리 수 제한 테스트가 가능해졌습니다.
3. **회귀 방지**: `@MeasurePerformance(maxQueries = n)` 어노테이션으로 CI에서 자동 검증됩니다.

<br><br>

## 변경 파일 목록

| 파일 | 변경 내용 |
|------|-----------|
| `build.gradle` | datasource-proxy 의존성 추가 |
| `ScheduleService.java` | N+1 해결 로직 적용 |
| `ScheduleCustomRepository.java` | 인터페이스에 메서드 시그니처 추가 |
| `ScheduleRepositoryImpl.java` | IN 쿼리 메서드 구현 |
| `DataSourceProxyConfig.java` | 쿼리 가로채기 설정 |
| `QueryCounter.java` | 쿼리 수 카운팅 유틸 |
| `MeasurePerformance.java` | 성능 측정 어노테이션 |
| `PerformanceExtension.java` | JUnit 5 Extension 구현 |
| `ScheduleApiTest.java` | N+1 방지 검증 테스트 |
| `application.properties (test)` | 테스트 환경 설정 |
