# 관리자 CRUD API 명세서

## 개요

| 항목 | 값 |
|------|-----|
| Base URL | `/admin/api/v1` |
| 인증 | JWT Bearer Token (`Authorization: Bearer {token}`) |
| 권한 | `ADMIN` Role 필요 |
| Content-Type | `application/json` |

---

## 공통 응답 형식

### 성공 응답
```json
{
  "data": { ... },
  "status": 200
}
```

### 페이징 응답
```json
{
  "content": [ ... ],
  "totalElements": 100,
  "totalPages": 4,
  "size": 25,
  "number": 0
}
```

### 에러 응답
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "상세 에러 메시지",
  "path": "/admin/api/v1/members/1"
}
```

### HTTP Status Codes

| Status | 설명 |
|--------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

---

## 1. Member (회원) API

### 1.1 회원 목록 조회

```
GET /admin/api/v1/members
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| username | String | No | - | 사용자명 검색 |
| authProvider | String | No | - | 인증 제공자 (google, none) |
| mainCharacter | String | No | - | 대표 캐릭터명 검색 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "memberId": 1,
      "username": "user@email.com",
      "createdDate": "2024-01-15T10:30:00",
      "authProvider": "google",
      "mainCharacter": "캐릭터명",
      "apiKey": "로스트아크 API Key"
    }
  ],
  "totalElements": 100,
  "totalPages": 4,
  "size": 25,
  "number": 0
}
```

---

### 1.2 회원 상세 조회

```
GET /admin/api/v1/members/{memberId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| memberId | Long | 회원 ID |

**Response**

```json
{
  "memberId": 1,
  "username": "user@email.com",
  "authProvider": "google",
  "apiKey": "로스트아크 API Key",
  "mainCharacter": "대표캐릭터명",
  "role": "USER",
  "adsDate": "2024-12-31T23:59:59",
  "createdDate": "2024-01-15T10:30:00",
  "characters": [
    {
      "characterId": 1,
      "characterName": "캐릭터명",
      "serverName": "루페온",
      "itemLevel": 1620.00,
      "characterClassName": "블레이드"
    }
  ]
}
```

---

### 1.3 회원 정보 수정

```
PUT /admin/api/v1/members/{memberId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| memberId | Long | 회원 ID |

**Request Body**

```json
{
  "role": "ADMIN",
  "mainCharacter": "새대표캐릭터명",
  "adsDate": "2025-12-31T23:59:59"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| role | String | No | 권한 (USER, ADMIN, PUBLISHER) |
| mainCharacter | String | No | 대표 캐릭터명 |
| adsDate | LocalDateTime | No | 광고 제거 만료 일시 |

**Response**

```json
{
  "memberId": 1,
  "username": "user@email.com",
  "role": "ADMIN",
  "mainCharacter": "새대표캐릭터명",
  "adsDate": "2025-12-31T23:59:59"
}
```

**비즈니스 로직**
- `mainCharacter` 변경 시 해당 회원의 캐릭터 목록에 존재하는지 검증

---

### 1.4 회원 삭제

```
DELETE /admin/api/v1/members/{memberId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| memberId | Long | 회원 ID |

**Response**: `200 OK`

**비즈니스 로직**
- 연관된 Characters, Comments, Friends, Notification 모두 Cascade 삭제

---

## 2. Character (캐릭터) API

### 2.1 캐릭터 목록 조회

```
GET /admin/api/v1/characters
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| memberId | Long | No | - | 특정 회원의 캐릭터만 조회 |
| serverName | String | No | - | 서버명 필터 |
| characterName | String | No | - | 캐릭터명 검색 |
| characterClassName | String | No | - | 직업 필터 |
| minItemLevel | Double | No | - | 최소 아이템 레벨 |
| maxItemLevel | Double | No | - | 최대 아이템 레벨 |
| isDeleted | Boolean | No | false | 삭제된 캐릭터 포함 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "characterId": 1,
      "memberId": 1,
      "memberUsername": "user@email.com",
      "serverName": "루페온",
      "characterName": "캐릭터명",
      "characterLevel": 60,
      "characterClassName": "블레이드",
      "characterImage": "https://...",
      "itemLevel": 1620.00,
      "sortNumber": 1,
      "goldCharacter": true,
      "isDeleted": false,
      "createdDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 500,
  "totalPages": 20,
  "size": 25,
  "number": 0
}
```

---

### 2.2 캐릭터 상세 조회

```
GET /admin/api/v1/characters/{characterId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| characterId | Long | 캐릭터 ID |

**Response**

```json
{
  "characterId": 1,
  "memberId": 1,
  "memberUsername": "user@email.com",
  "serverName": "루페온",
  "characterName": "캐릭터명",
  "characterLevel": 60,
  "characterClassName": "블레이드",
  "characterImage": "https://...",
  "itemLevel": 1620.00,
  "combatPower": 150000.00,
  "sortNumber": 1,
  "memo": "메인 캐릭터",
  "goldCharacter": true,
  "challengeGuardian": false,
  "challengeAbyss": false,
  "isDeleted": false,
  "dayTodo": {
    "chaosCheck": 2,
    "chaosGauge": 40,
    "guardianCheck": 1,
    "guardianGauge": 20,
    "eponaCheck": 3,
    "eponaGauge": 0
  },
  "weekTodo": {
    "weekEpona": 3,
    "silmaelChange": true,
    "cubeTicket": 5
  },
  "settings": {
    "showCharacter": true,
    "showChaos": true,
    "showGuardian": true
  },
  "createdDate": "2024-01-15T10:30:00"
}
```

---

### 2.3 캐릭터 정보 수정

```
PUT /admin/api/v1/characters/{characterId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| characterId | Long | 캐릭터 ID |

**Request Body**

```json
{
  "characterName": "새캐릭터명",
  "itemLevel": 1640.00,
  "sortNumber": 1,
  "memo": "수정된 메모",
  "goldCharacter": true,
  "isDeleted": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| characterName | String | No | 캐릭터명 |
| itemLevel | Double | No | 아이템 레벨 |
| sortNumber | Integer | No | 정렬 순서 |
| memo | String | No | 메모 (최대 100자) |
| goldCharacter | Boolean | No | 골드 획득 지정 |
| isDeleted | Boolean | No | 삭제 여부 (복구 시 false) |

**Response**

```json
{
  "characterId": 1,
  "characterName": "새캐릭터명",
  "itemLevel": 1640.00,
  "sortNumber": 1,
  "memo": "수정된 메모",
  "goldCharacter": true,
  "isDeleted": false
}
```

---

### 2.4 캐릭터 삭제

```
DELETE /admin/api/v1/characters/{characterId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| characterId | Long | 캐릭터 ID |

**Query Parameters**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| hardDelete | Boolean | false | 영구 삭제 여부 |

**Response**: `200 OK`

**비즈니스 로직**
- `hardDelete=false`: Soft Delete (isDeleted = true)
- `hardDelete=true`: 영구 삭제 (연관 TodoV2 포함)

---

## 3. Content (컨텐츠) API

### 3.1 컨텐츠 목록 조회

```
GET /admin/api/v1/contents
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| contentType | String | No | - | day, week, cube |
| category | String | No | - | 카테고리 필터 |
| name | String | No | - | 이름 검색 |
| minLevel | Double | No | - | 최소 레벨 |
| maxLevel | Double | No | - | 최대 레벨 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "id": 1,
      "contentType": "day",
      "name": "카오스던전 1620",
      "level": 1620.0,
      "category": "카오스던전"
    },
    {
      "id": 10,
      "contentType": "week",
      "name": "카멘 하드 1관문",
      "level": 1630.0,
      "category": "군단장레이드",
      "weekCategory": "카멘",
      "weekContentCategory": "하드",
      "gate": 1,
      "gold": 5500
    }
  ],
  "totalElements": 50,
  "totalPages": 2,
  "size": 25,
  "number": 0
}
```

---

### 3.2 컨텐츠 상세 조회

```
GET /admin/api/v1/contents/{contentId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| contentId | Long | 컨텐츠 ID |

**Response (DayContent)**

```json
{
  "id": 1,
  "contentType": "day",
  "name": "카오스던전 1620",
  "level": 1620.0,
  "category": "카오스던전",
  "shilling": 150000.0,
  "honorShard": 2000.0,
  "leapStone": 15.0,
  "destructionStone": 600.0,
  "guardianStone": 1200.0,
  "jewelry": 0.5
}
```

**Response (WeekContent)**

```json
{
  "id": 10,
  "contentType": "week",
  "name": "카멘 하드 1관문",
  "level": 1630.0,
  "category": "군단장레이드",
  "weekCategory": "카멘",
  "weekContentCategory": "하드",
  "gate": 1,
  "gold": 5500,
  "characterGold": 0,
  "coolTime": 1,
  "moreRewardGold": 1800
}
```

**Response (CubeContent)**

```json
{
  "id": 20,
  "contentType": "cube",
  "name": "에브니 큐브 1620",
  "level": 1620.0,
  "category": "에브니큐브",
  "jewelry": 1.5,
  "leapStone": 20.0,
  "shilling": 50000.0,
  "solarGrace": 10.0,
  "solarBlessing": 5.0,
  "solarProtection": 2.0,
  "cardExp": 100.0,
  "lavasBreath": 3.0,
  "glaciersBreath": 3.0
}
```

---

### 3.3 컨텐츠 추가

```
POST /admin/api/v1/contents
```

**Request Body (DayContent)**

```json
{
  "contentType": "day",
  "name": "카오스던전 1700",
  "level": 1700.0,
  "category": "카오스던전",
  "shilling": 200000.0,
  "honorShard": 3000.0,
  "leapStone": 20.0,
  "destructionStone": 800.0,
  "guardianStone": 1600.0,
  "jewelry": 1.0
}
```

**Request Body (WeekContent)**

```json
{
  "contentType": "week",
  "name": "에기르 하드 1관문",
  "level": 1680.0,
  "category": "군단장레이드",
  "weekCategory": "에기르",
  "weekContentCategory": "하드",
  "gate": 1,
  "gold": 7000,
  "characterGold": 0,
  "coolTime": 1,
  "moreRewardGold": 2000
}
```

**Request Body (CubeContent)**

```json
{
  "contentType": "cube",
  "name": "에브니 큐브 1700",
  "level": 1700.0,
  "category": "에브니큐브",
  "jewelry": 2.0,
  "leapStone": 25.0,
  "shilling": 60000.0,
  "solarGrace": 12.0,
  "solarBlessing": 6.0,
  "solarProtection": 3.0,
  "cardExp": 120.0,
  "lavasBreath": 4.0,
  "glaciersBreath": 4.0
}
```

**DayContent 필드**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| contentType | String | Yes | "day" |
| name | String | Yes | 컨텐츠명 |
| level | Double | Yes | 입장 레벨 |
| category | String | Yes | 카테고리 |
| shilling | Double | No | 실링 |
| honorShard | Double | No | 명예의 파편 |
| leapStone | Double | No | 돌파석 |
| destructionStone | Double | No | 파괴석 |
| guardianStone | Double | No | 수호석 |
| jewelry | Double | No | 1레벨 보석 |

**WeekContent 필드**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| contentType | String | Yes | "week" |
| name | String | Yes | 컨텐츠명 |
| level | Double | Yes | 입장 레벨 |
| category | String | Yes | 카테고리 |
| weekCategory | String | Yes | 레이드 이름 |
| weekContentCategory | String | Yes | 난이도 |
| gate | Integer | Yes | 관문 번호 |
| gold | Integer | No | 골드 |
| characterGold | Integer | No | 캐릭터 귀속 골드 |
| coolTime | Integer | No | 주기 (1=매주) |
| moreRewardGold | Integer | No | 더보기 골드 |

**CubeContent 필드**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| contentType | String | Yes | "cube" |
| name | String | Yes | 컨텐츠명 |
| level | Double | Yes | 입장 레벨 |
| category | String | Yes | 카테고리 |
| jewelry | Double | No | 1레벨 보석 |
| leapStone | Double | No | 돌파석 |
| shilling | Double | No | 실링 |
| solarGrace | Double | No | 태양의 은총 |
| solarBlessing | Double | No | 태양의 축복 |
| solarProtection | Double | No | 태양의 가호 |
| cardExp | Double | No | 카드 경험치 |
| lavasBreath | Double | No | 용암의 숨결 |
| glaciersBreath | Double | No | 빙하의 숨결 |

**Response**

```json
{
  "id": 25,
  "contentType": "day",
  "name": "카오스던전 1700",
  "level": 1700.0,
  "category": "카오스던전"
}
```

---

### 3.4 컨텐츠 수정

```
PUT /admin/api/v1/contents/{contentId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| contentId | Long | 컨텐츠 ID |

**Request Body**: 추가 API와 동일 (contentType 변경 불가)

**Response**

```json
{
  "id": 25,
  "contentType": "week",
  "name": "카멘 하드 1관문 (수정)",
  "level": 1630.0,
  "gold": 6000
}
```

**비즈니스 로직**
- `contentType`은 변경 불가
- 수정 시 캐시 무효화

---

### 3.5 컨텐츠 삭제

```
DELETE /admin/api/v1/contents/{contentId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| contentId | Long | 컨텐츠 ID |

**Response**: `200 OK`

**Error Cases**
- `400`: 사용 중인 컨텐츠 (TodoV2에서 참조 중)

---

## 4. Dashboard (대시보드) API

### 4.1 어드민 회원 정보

```
GET /admin/api/v1/dashboard/member
```

**Response**

```json
{
  "memberId": 1,
  "username": "admin@email.com",
  "role": "ADMIN",
  "mainCharacter": "캐릭터명"
}
```

---

### 4.2 일일 가입자 수 통계

```
GET /admin/api/v1/dashboard/daily-members
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | int | No | 14 | 조회 일수 |

**Response**

```json
[
  { "date": "2024-01-15", "count": 50 },
  { "date": "2024-01-14", "count": 45 },
  { "date": "2024-01-13", "count": 60 }
]
```

---

### 4.3 일일 가입 캐릭터 수 통계

```
GET /admin/api/v1/dashboard/daily-characters
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | int | No | 14 | 조회 일수 |

**Response**

```json
[
  { "date": "2024-01-15", "count": 200 },
  { "date": "2024-01-14", "count": 180 },
  { "date": "2024-01-13", "count": 250 }
]
```

---

### 4.4 전체 통계 요약

```
GET /admin/api/v1/dashboard/summary
```

**Response**

```json
{
  "totalMembers": 10000,
  "totalCharacters": 60000,
  "todayNewMembers": 50,
  "todayNewCharacters": 200,
  "activeMembers": 5000
}
```

---

### 4.5 최근 활동 조회

```
GET /admin/api/v1/dashboard/recent-activities
```

**Request Parameters**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| limit | int | 10 | 조회할 항목 수 |

**Response**

```json
[
  {
    "type": "NEW_MEMBER",
    "message": "새 회원 가입",
    "detail": "user1234",
    "createdDate": "2024-01-15T10:30:00"
  },
  {
    "type": "NEW_CHARACTER",
    "message": "캐릭터 등록",
    "detail": "광전사 1620",
    "createdDate": "2024-01-15T10:18:00"
  }
]
```

**활동 유형**

| Type | Message | Detail |
|------|---------|--------|
| NEW_MEMBER | 새 회원 가입 | 회원 username |
| NEW_CHARACTER | 캐릭터 등록 | 클래스명 아이템레벨 |

---

## 5. Comments (댓글) API

### 5.1 댓글 목록 조회

```
GET /admin/api/v1/comments
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| memberId | Long | No | - | 특정 회원의 댓글 |
| body | String | No | - | 댓글 내용 검색 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "commentId": 1,
      "memberId": 1,
      "memberUsername": "user@email.com",
      "body": "댓글 내용",
      "parentId": null,
      "createdDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 4,
  "size": 25,
  "number": 0
}
```

---

### 5.2 댓글 삭제

```
DELETE /admin/api/v1/comments/{commentId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| commentId | Long | 댓글 ID |

**Response**: `200 OK`

---

## 6. Friends (깐부) API

### 6.1 깐부 관계 목록 조회

```
GET /admin/api/v1/friends
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| memberId | Long | No | - | 특정 회원의 깐부 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "friendId": 1,
      "memberId": 1,
      "memberUsername": "user1@email.com",
      "friendUsername": "user2@email.com",
      "areWeFriend": true,
      "createdDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 2,
  "size": 25,
  "number": 0
}
```

---

### 6.2 깐부 관계 삭제

```
DELETE /admin/api/v1/friends/{friendId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| friendId | Long | 깐부 관계 ID |

**Response**: `200 OK`

---

## 7. Notification (알림) API

### 7.1 알림 목록 조회

```
GET /admin/api/v1/notifications
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| memberId | Long | No | - | 특정 회원의 알림 |
| isRead | Boolean | No | - | 읽음 여부 필터 |
| page | int | No | 1 | 페이지 번호 |
| limit | int | No | 25 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "notificationId": 1,
      "receiverId": 1,
      "receiverUsername": "user@email.com",
      "content": "알림 내용",
      "isRead": false,
      "createdDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 4,
  "size": 25,
  "number": 0
}
```

---

### 7.2 전체 공지 발송

```
POST /admin/api/v1/notifications/broadcast
```

**Request Body**

```json
{
  "content": "전체 공지 내용입니다."
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| content | String | Yes | 공지 내용 |

**Response**

```json
{
  "message": "전체 공지가 발송되었습니다.",
  "sentCount": 10000
}
```

---

### 7.3 알림 삭제

```
DELETE /admin/api/v1/notifications/{notificationId}
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| notificationId | Long | 알림 ID |

**Response**: `200 OK`

---

## 8. Ads (후원) API

### 8.1 후원 목록 조회

```
GET /admin/api/v1/ads
```

**Request Parameters**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| adsId | Long | No | - | 커서 기반 페이징용 ID |
| limit | int | No | 20 | 페이지당 항목 수 |

**Response**

```json
{
  "content": [
    {
      "adsId": 1,
      "createdDate": "2024-01-15T10:30:00",
      "name": "후원자명",
      "proposerEmail": "sponsor@email.com",
      "memberId": 123,
      "checked": false
    }
  ],
  "hasNext": true
}
```

---

### 8.2 광고 제거 날짜 변경

```
POST /admin/api/v1/ads/date
```

**Request Body**

```json
{
  "proposerEmail": "user@email.com",
  "price": 5000
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| proposerEmail | String | Yes | 대상 회원 이메일 |
| price | Long | Yes | 후원 금액 (광고 제거 기간 계산용) |

**Response**: `200 OK`

**비즈니스 로직**
- price 기반으로 광고 제거 기간 계산하여 회원의 adsDate 업데이트

---

## Enum 참조

### Role (회원 권한)
| 값 | 설명 |
|----|------|
| USER | 일반 사용자 |
| ADMIN | 관리자 |
| PUBLISHER | 퍼블리셔 |

### Category (컨텐츠 카테고리)
| 값 | 설명 |
|----|------|
| 카오스던전 | 일일 카오스 던전 |
| 가디언토벌 | 일일 가디언 토벌 |
| 일일에포나 | 일일 에포나 의뢰 |
| 군단장레이드 | 주간 군단장 레이드 |
| 어비스던전 | 주간 어비스 던전 |
| 어비스레이드 | 주간 어비스 레이드 |
| 에브니큐브 | 큐브 컨텐츠 |

### WeekContentCategory (주간 컨텐츠 난이도)
| 값 | 설명 |
|----|------|
| 노말 | 노말 난이도 |
| 하드 | 하드 난이도 |
| 싱글 | 싱글 모드 |
| 나이트메어 | 나이트메어 모드 |

---

## 인증 예시

```http
GET /admin/api/v1/members HTTP/1.1
Host: api.example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```
