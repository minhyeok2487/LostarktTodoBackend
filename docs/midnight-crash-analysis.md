# 자정 서버 크래시 장애 분석 및 개선 계획

## 1. 장애 현상

- **발생 시점:** 2026-02-12 00:25:38 (자정 전후 반복 발생)
- **증상:** 서버 메모리 급증 + 응답 불가 + 서버 다운
- **영향:** 모든 API 요청 실패

## 2. 근본 원인

### 2-1. DB 커넥션 풀 고갈

HikariCP 커넥션 풀(최대 10개)이 고갈되면서 모든 요청이 커넥션 대기 상태에 빠짐.

```
HikariPool-1 - Connection is not available, request timed out after 20000ms.
Unable to acquire JDBC Connection
```

- **Tomcat 스레드:** 정상 시 exec-1~21 → 장애 시 exec-27~82 (82개 스레드 폭증)
- **타임아웃:** `connection-timeout=20000` (20초) 후 일괄 실패

### 2-2. 자정 스케줄러 동시 실행

00:00 정각에 3개 스케줄 작업이 동시 실행되며 커넥션 경합 발생:

| 작업 | cron | 커넥션 패턴 |
|------|------|------------|
| `addEnergyToAllLifeEnergies` | `0 0,30 * * * *` | REQUIRES_NEW, 525건 bulk UPDATE |
| `checkScheduleRaids` | `0 */10 * * * *` | REQUIRES_NEW, 2개 bulk UPDATE |
| `fetchScheduledInspectionData` | `0 0 * * * ?` | 캐릭터당 7개 API + DB 저장 |

### 2-3. 캐스케이드 실패

```
커넥션 풀 고갈
  → 요청 20초 대기 후 타임아웃
  → ExControllerAdvice에서 동기 Discord 웹훅 호출
  → Discord 429 Rate Limit (Too Many Requests)
  → ExceptionHandler 내부 2차 에러
  → 에러 처리 자체 불가능
  → 서버 완전 마비
```

### 2-4. 추가 원인

- `spring.jpa.open-in-view` 미설정 (기본값 `true`) → 모든 HTTP 요청이 응답 완료까지 커넥션 점유
- `WebHookService.send()`가 매번 `new RestTemplate()` 생성 + 동기 블로킹 호출
- `WebHookService.callEvent()`에 타임아웃 미설정

## 3. 적용된 긴급 수정 (Phase 1)

### 3-1. WebHookService 비동기화
- `callEvent()`에 `@Async("taskExecutor")` 적용 → 에러 핸들러 스레드 블로킹 차단
- `RestTemplate` 싱글턴화 + 타임아웃 설정 (connect: 3초, read: 5초)
- 웹훅 실패 시 예외를 전파하지 않음 (log.warn만 출력)
- `sendMessage()`의 IOException도 RuntimeException 대신 log.warn 처리

### 3-2. HikariCP 커넥션 풀 증가
| 설정 | 변경 전 | 변경 후 |
|------|--------|--------|
| `maximum-pool-size` | 10 | 20 |
| `idle-timeout` | 30000 | 60000 |
| `connection-timeout` | 20000 | 30000 |
| `leak-detection-threshold` | 없음 | 60000 |

### 3-3. 스케줄 시간 분산
- `addEnergyToAllLifeEnergies`: `0 0,30` → `0 5,35` (5분 오프셋)
- `fetchScheduledInspectionData`: `0 0 * * * ?` → `0 2 * * * ?` (2분 오프셋)

### 3-4. WebHook 쿨다운 및 블랙리스트

#### 쿨다운 (동일 예외 5분 제한)
- `ConcurrentHashMap<String, Long>`으로 예외 클래스별 마지막 전송 시각 관리
- 동일 예외가 5분 내에 재발생하면 Discord 전송 생략, `log.debug`만 출력
- 첫 번째 에러는 즉시 전송되므로 원인 파악 지연 없음
- 메모리 사용: 예외 종류 수 × ~100바이트 (실질적으로 무시 가능)

#### 블랙리스트 (비서버 에러 제외)
- 에러 메시지에 아래 키워드가 포함되면 Discord 웹훅 미전송 (로그만 남김):
  - `"점검중"` — 로스트아크 서버 정기점검 (우리 버그 아님)
  - `"올바르지 않은 apiKey"` — 유저의 크롬 자동 번역으로 인한 API 키 오류
- 블랙리스트 제외 시에도 `log.warn`으로 기록되므로 서버 로그에서 확인 가능

#### 효과 (실제 Discord 로그 기준)
| 에러 유형 | 수정 전 (2일간) | 수정 후 (예상) |
|----------|:-:|:-:|
| CannotCreateTransactionException (자정 크래시) | 20+ | 1 (쿨다운) |
| "로스트아크 서버가 점검중" | 8+ | 0 (블랙리스트) |
| "올바르지 않은 apiKey" | 10+ | 0 (블랙리스트) |
| CombatPower null 파싱 에러 | 4+ | 1 (쿨다운) |

## 4. 향후 개선 계획

### Phase 2: MySQL EVENT (순수 SQL 작업 DB 이전)

`addEnergyToAllLifeEnergies`를 MySQL EVENT로 이전:
```sql
CREATE EVENT IF NOT EXISTS evt_add_life_energy
ON SCHEDULE EVERY 30 MINUTE
STARTS TIMESTAMP(CURRENT_DATE, '00:05:00')
DO
  UPDATE life_energy
  SET energy = LEAST(max_energy, energy + (CASE WHEN beatrice = 1 THEN 99 ELSE 90 END));
```

`checkScheduleRaids`는 QueryDSL 변환이 복잡하여 서버에 유지.

### Phase 3: API + Lambda (외부 API 의존 작업 이전)

Lambda가 Internal API를 호출하는 구조 (NAT Gateway 불필요):

```
Lambda (VPC 밖) → HTTP POST → Spring Boot Internal API → DB
```

| 엔드포인트 | 대상 작업 | EventBridge 스케줄 |
|-----------|----------|-------------------|
| `POST /internal/schedule/market-data` | 거래소 데이터 갱신 | 매일 01:00 |
| `POST /internal/schedule/day-reset` | 일일 숙제 초기화 | 매일 06:00 |
| `POST /internal/schedule/week-reset` | 주간 숙제 초기화 | 수 06:02 |
| `POST /internal/schedule/inspection` | 군장검사 데이터 수집 | 매시 정각+2분 |

보안: `X-Internal-Api-Key` 헤더 검증 필터 적용.

### Phase 4: OSIV 비활성화 (별도 PR)

`spring.jpa.open-in-view=false` 설정 후 LazyInitializationException 발생 지점 수정.

## 5. 최종 목표 아키텍처

```
서버 (Spring Boot):
  └── checkScheduleRaids (10분, 빠른 bulk UPDATE)

MySQL EVENT:
  └── addEnergyToAllLifeEnergies (30분)

Lambda + EventBridge:
  ├── updateMarketData (매일 01:00)
  ├── resetDayTodo (매일 06:00)
  ├── resetWeekTodo (수 06:02)
  └── fetchScheduledInspectionData (매시 정각+2분)

HikariCP: max=20, OSIV=false
WebHook: 비동기, 타임아웃 설정, 실패 무시
```

## 6. 부하 테스트 결과 (Phase 1 적용 후)

### 테스트 환경
- Docker: Corretto 17, `-Xms256m -Xmx512m` (운영 동일)
- MySQL 8.0 (Docker, tmpfs)
- 컨테이너 메모리 제한: 768MB

### 테스트 방법
DB를 직접 조회하는 공개 엔드포인트(`/api/v1/community`, `/api/v1/auth/login`)에
동시 요청을 50 → 100 → 200 → 500 → 1000개까지 점진적으로 증가시켜 부하 인가.

### 결과

| 동시 요청 | 컨테이너 메모리 | HikariCP (active/idle/max) | 스레드 (peak) | 타임아웃 | 서버 상태 |
|-----------|----------------|---------------------------|--------------|---------|----------|
| 0 (초기) | 528MB / 768MB (68.7%) | 0 / 5 / 20 | 27 | 0 | OK |
| 50 | 532MB / 768MB (69.3%) | 0 / 16 / 20 | 46 | 0 | OK |
| 100 | 535MB / 768MB (69.7%) | 0 / 19 / 20 | 57 | 0 | OK |
| 200 | 551MB / 768MB (71.8%) | 0 / 20 / 20 | 57 | 0 | OK |
| 500 | 547MB / 768MB (71.3%) | 0 / 20 / 20 | 57 | 0 | OK |
| 1000 | 556MB / 768MB (72.5%) | 0 / 20 / 20 | 64 | 0 | OK |
| 회복 (30s) | 556MB / 768MB (72.5%) | 0 / 20 / 20 | 63 | **0** | OK |

### 분석
- **HikariCP 타임아웃 0회:** 커넥션 풀 20개로 증가 후, 1000개 동시 요청에서도 타임아웃 없음
- **메모리 안정:** 초기 528MB → 최대 556MB (28MB 증가, 768MB 제한 내 안정)
- **스레드 제어:** 피크 64개로 이전 장애 시 82개보다 낮음
- **회복:** 부하 종료 후 30초 만에 정상 상태 복귀

### 이전 장애 상황과 비교

| 항목 | 장애 시 (수정 전) | 부하 테스트 (수정 후) |
|------|-----------------|-------------------|
| HikariCP max | 10 | 20 |
| 커넥션 타임아웃 | 다수 발생 (20s) | **0회** |
| 스레드 피크 | 82+ | 64 |
| 웹훅 캐스케이드 | 발생 (429 에러) | 차단 (@Async) |
| 서버 상태 | **다운** | **정상** |

## 7. 모니터링

- Actuator: `/manage/prometheus`
  - `hikaricp_connections_active` - 활성 커넥션 수
  - `hikaricp_connections_pending` - 대기 중 커넥션 요청 수
  - `hikaricp_connections_timeout_total` - 타임아웃 횟수
- `leak-detection-threshold=60000` → 60초 이상 반환되지 않는 커넥션 경고 로그
