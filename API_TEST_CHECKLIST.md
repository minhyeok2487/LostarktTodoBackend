# API 테스트 체크리스트

> 총 API 클래스: 33개 | 총 엔드포인트: 120개+

---

## domain

### IndexApi (`/`)
- [x] `GET /` - index *(단순 health check, 문제 없음)*

---

### admin/api

#### AdminAdsApi (`/admin/api/v1/ads`)
- [ ] `GET /admin/api/v1/ads` - search
- [ ] `POST /admin/api/v1/ads/date` - updateAdsDate

#### AdminMainController (`/admin`)
- [ ] `GET /admin/member` - getMember
- [ ] `GET /admin/dash-board/member` - searchMemberDashBoard
- [ ] `GET /admin/dash-board/characters` - searchCharactersDashBoard

#### AdminMemberController (`/admin/members`)
- [ ] `GET /admin/members` - search

#### AdminContentController (`/admin/api/v1/content`)
- [ ] `POST /admin/api/v1/content` - addContent

---

### analysis/api

#### AnalysisApi (`/api/v1/analysis`)
- [x] `GET /api/v1/analysis` - searchAnalysis
- [x] `POST /api/v1/analysis` - updateAnalysis *(단순 저장 로직, 문제 없음)*

---

### board/comments/api

#### CommentsApi (`/api/v1/comments`)
- [x] `GET /api/v1/comments` - searchComments *(쿼리 12개 발생, N+1 문제 의심)*

---

### board/community/api

#### CommunityApi (`/api/v1/community`)
- [x] `GET /api/v1/community/category` - getCommunityCategory *(단순 enum 반환, 문제 없음)*
- [x] `GET /api/v1/community` - search *(쿼리 23개 발생, N+1 최적화 필요)*
- [x] `GET /api/v1/community/{communityId}` - get *(정상 동작)*
- [ ] `POST /api/v1/community` - save *(Discord webhook 연동으로 테스트 스킵)*
- [ ] `POST /api/v1/community/image` - uploadImage *(S3 연동으로 테스트 스킵)*
- [ ] `PATCH /api/v1/community` - update *(Discord webhook 연동으로 테스트 스킵)*
- [ ] `DELETE /api/v1/community/{communityId}` - delete *(Discord webhook 연동으로 테스트 스킵)*
- [ ] `POST /api/v1/community/like/{communityId}` - updateLike *(Discord webhook 연동으로 테스트 스킵)*

#### FollowApi (`/api/v1/follow`)
- [x] `GET /api/v1/follow` - search *(정상 동작)*
- [ ] `POST /api/v1/follow` - update

---

### character/api

#### CharacterApi (`/api/v1/character`)
- [x] `PATCH /api/v1/character/settings` - updateSettings *(정상 동작)*
- [x] `PATCH /api/v1/character/gold-character` - updateGoldCharacter *(정상 동작)*
- [x] `POST /api/v1/character/memo` - updateMemo *(정상 동작)*
- [x] `PATCH /api/v1/character/deleted` - updateCharacterStatus *(정상 동작)*
- [ ] `PUT /api/v1/character` - updateCharacter *(외부 API 연동 필요)*
- [ ] `PATCH /api/v1/character/name` - updateCharacterName *(외부 API 연동 필요)*
- [ ] `POST /api/v1/character` - addCharacter *(외부 API 연동 필요)*

#### CharacterDayApi (`/api/v1/character/day`)
- [x] `POST /api/v1/character/day/check` - updateDayCheck *(정상 동작)*
- [x] `POST /api/v1/character/day/gauge` - updateDayGauge *(정상 동작)*
- [x] `POST /api/v1/character/day/check/all` - updateDayCheckAll *(정상 동작)*
- [x] `POST /api/v1/character/day/check/all-characters` - updateDayCheckAllCharacters *(정상 동작)*

#### CharacterWeekApi (`/api/v1/character/week`)
- [ ] `POST /api/v1/character/week/raid` - updateWeekRaid
- [x] `GET /api/v1/character/week/raid/form` - getTodoForm *(정상 동작)*
- [ ] `POST /api/v1/character/week/raid/bus` - updateWeekRaidBusGold
- [ ] `POST /api/v1/character/week/raid/check` - updateWeekRaidCheck
- [ ] `POST /api/v1/character/week/raid/message` - updateWeekRaidMessage
- [ ] `POST /api/v1/character/week/raid/sort` - updateWeekRaidSort
- [x] `POST /api/v1/character/week/epona` - updateWeekEpona *(정상 동작)*
- [x] `POST /api/v1/character/week/silmael` - updateWeekSilmael *(정상 동작)*
- [x] `POST /api/v1/character/week/cube` - updateWeekCubeTicket *(정상 동작)*
- [ ] `PATCH /api/v1/character/week/raid/gold-check` - updateRaidGoldCheck
- [x] `PATCH /api/v1/character/week/gold-check-version` - updateGoldCheckVersion *(정상 동작)*
- [ ] `POST /api/v1/character/week/raid/more-reward` - updateRaidMoreRewardCheck
- [x] `POST /api/v1/character/week/elysian` - updateElysian *(정상 동작)*
- [x] `POST /api/v1/character/week/elysian/all` - updateElysianAll *(정상 동작)*

#### CharacterListApi (`/api/v1/character-list`)
- [x] `GET /api/v1/character-list` - get *(다수 쿼리 발생 - N+1 최적화 필요)*
- [x] `PATCH /api/v1/character-list/sorting` - updateSort *(정상 동작)*
- [x] `GET /api/v1/character-list/deleted` - getDeletedCharacter *(정상 동작)*

#### CustomTodoApi (`/api/v1/custom`)
- [x] `GET /api/v1/custom` - search *(정상 동작)*
- [x] `POST /api/v1/custom` - create *(정상 동작)*
- [ ] `PATCH /api/v1/custom/{customTodoId}` - update
- [ ] `POST /api/v1/custom/check` - check
- [ ] `DELETE /api/v1/custom/{customTodoId}` - remove

---

### content/api

#### WeekContentApi (`/api/v1/content/week`)
- [x] `GET /api/v1/content/week/raid/category` - getScheduleRaidCategory *(정상 동작)*

---

### cube/api

#### CubeApi (`/api/v1/cube`)
- [x] `GET /api/v1/cube/statistics` - getStatistics *(정상 동작)*
- [x] `GET /api/v1/cube` - get *(정상 동작)*
- [x] `POST /api/v1/cube` - create *(정상 동작)*
- [ ] `PUT /api/v1/cube` - update
- [ ] `DELETE /api/v1/cube/{characterId}` - delete
- [ ] `POST /api/v1/cube/spend` - spendWeekCubeTicket

---

### friend/api

#### FriendApi (`/api/v1/friend`)
- [x] `GET /api/v1/friend` - get *(심각한 N+1 문제 - 60개 이상 쿼리 발생)*
- [ ] `GET /api/v1/friend/character/{characterName}` - findCharacter
- [ ] `POST /api/v1/friend` - friendsRequest
- [ ] `POST /api/v1/friend/request` - updateFriendsRequest
- [ ] `PATCH /api/v1/friend/settings` - updateFriendSetting
- [ ] `DELETE /api/v1/friend/{friendId}` - delete
- [ ] `PUT /api/v1/friend/sort` - updateSort

---

### logs/api

#### LogsApi (`/api/v1/logs`)
- [x] `GET /api/v1/logs` - search *(정상 동작)*
- [ ] `GET /api/v1/logs/profit` - getLogsProfit *(403 에러 발생)*
- [ ] `DELETE /api/v1/logs/{logId}` - delete
- [x] `POST /api/v1/logs` - saveEtcLog *(정상 동작)*

---

### member/api

#### AuthApi (`/api/v1/auth`)
- [ ] `POST /api/v1/auth/signup` - signUp
- [ ] `POST /api/v1/auth/login` - loginMember
- [ ] `GET /api/v1/auth/logout` - logout

#### EmailApi (`/api/v1/mail`)
- [ ] `POST /api/v1/mail` - sendSignUpMail
- [ ] `POST /api/v1/mail/password` - sendResetPasswordMail
- [ ] `POST /api/v1/mail/auth` - authMail

#### MemberApi (`/api/v1/member`)
- [x] `GET /api/v1/member` - get *(정상 동작)*
- [ ] `POST /api/v1/member/character` - saveCharacter
- [ ] `POST /api/v1/member/password` - updatePassword
- [ ] `PATCH /api/v1/member/main-character` - editMainCharacter
- [ ] `PATCH /api/v1/member/provider` - editProvider
- [ ] `POST /api/v1/member/ads` - saveAds
- [ ] `DELETE /api/v1/member/characters` - deleteCharacters
- [ ] `PATCH /api/v1/member/api-key` - updateApiKey

#### LifeEnergyApi (`/api/v1/life-energy`)
- [ ] `POST /api/v1/life-energy` - save
- [ ] `PUT /api/v1/life-energy` - update
- [ ] `DELETE /api/v1/life-energy/{characterName}` - deleteCharacterLifeEnergy
- [ ] `POST /api/v1/life-energy/spend` - spend

---

### notification/api

#### NotificationApi (`/api/v1/notification`)
- [x] `GET /api/v1/notification` - search *(정상 동작)*
- [x] `GET /api/v1/notification/status` - getRecent *(정상 동작)*
- [ ] `POST /api/v1/notification/{notificationId}` - updateRead
- [x] `POST /api/v1/notification/all` - updateReadAll *(정상 동작)*

---

### schedule/api

#### ScheduleApi (`/api/v1/schedule`)
- [x] `GET /api/v1/schedule` - search *(완료)*
- [x] `GET /api/v1/schedule/raid/category` - getScheduleRaidCategory *(정상 동작)*
- [x] `POST /api/v1/schedule` - create *(정상 동작)*
- [ ] `GET /api/v1/schedule/{scheduleId}` - get
- [ ] `PATCH /api/v1/schedule/{scheduleId}` - edit
- [ ] `DELETE /api/v1/schedule/{scheduleId}` - remove
- [ ] `POST /api/v1/schedule/{scheduleId}/friend` - editFriend

---

### generaltodo/api

#### GeneralTodoOverviewApi (`/api/v1/general-todos`)
- [x] `GET /api/v1/general-todos` - getOverview *(정상 동작)*

#### GeneralTodoFolderApi (`/api/v1/general-todos`)
- [x] `POST /api/v1/general-todos/folders` - createFolder *(정상 동작)*
- [ ] `PATCH /api/v1/general-todos/folders/{folderId}` - renameFolder
- [ ] `PATCH /api/v1/general-todos/folders/reorder` - reorderFolders
- [ ] `DELETE /api/v1/general-todos/folders/{folderId}` - deleteFolder

#### GeneralTodoCategoryApi (`/api/v1/general-todos`)
- [ ] `POST /api/v1/general-todos/categories/folders/{folderId}` - createCategory
- [ ] `PATCH /api/v1/general-todos/categories/{categoryId}` - updateCategory
- [ ] `PATCH /api/v1/general-todos/categories/folders/{folderId}/reorder` - reorderCategories
- [ ] `DELETE /api/v1/general-todos/categories/{categoryId}` - deleteCategory

#### GeneralTodoItemApi (`/api/v1/general-todos`)
- [ ] `POST /api/v1/general-todos/items` - createItem
- [ ] `PATCH /api/v1/general-todos/items/{itemId}` - updateItem
- [ ] `PATCH /api/v1/general-todos/items/{itemId}/status` - updateStatus
- [ ] `DELETE /api/v1/general-todos/items/{itemId}` - deleteItem

#### GeneralTodoStatusApi (`/api/v1/general-todos`)
- [ ] `POST /api/v1/general-todos/categories/{categoryId}/statuses` - createStatus
- [ ] `PATCH /api/v1/general-todos/categories/{categoryId}/statuses/{statusId}` - renameStatus
- [ ] `PATCH /api/v1/general-todos/categories/{categoryId}/statuses/reorder` - reorderStatuses
- [ ] `DELETE /api/v1/general-todos/categories/{categoryId}/statuses/{statusId}` - deleteStatus

---

### servertodo/api

#### ServerTodoApi (`/api/v1/server-todos`)
- [x] `POST /api/v1/server-todos` - createServerTodo *(정상 동작)*
- [x] `GET /api/v1/server-todos` - getServerTodos *(정상 동작)*
- [ ] `PATCH /api/v1/server-todos/{todoId}/toggle-enabled` - toggleEnabled
- [ ] `POST /api/v1/server-todos/{todoId}/check` - check

---

## domainMyGame

### mygame/api

#### MyGameApi (`/api/v1/games`)
- [ ] `GET /api/v1/games` - getGames
- [ ] `GET /api/v1/games/{id}` - getGameById
- [ ] `GET /api/v1/games/all` - getAllGames
- [ ] `POST /api/v1/games` - createGame
- [ ] `POST /api/v1/games/images` - uploadImage

---

### myevent/api

#### MyEventApi (`/api/v1/events`)
- [ ] `GET /api/v1/events` - getEvents
- [ ] `GET /api/v1/events/{id}` - getEventById
- [ ] `POST /api/v1/events` - createEvent
- [ ] `POST /api/v1/events/images` - uploadImage

---

### suggestion/api

#### SuggestionApi (`/api/v1/suggestions`)
- [ ] `POST /api/v1/suggestions` - createSuggestion

---

## 진행 상황

| 도메인 | 완료 | 전체 | 진행률 |
|--------|------|------|--------|
| admin | 0 | 7 | 0% |
| analysis | 1 | 2 | 50% |
| board | 0 | 10 | 0% |
| character | 0 | 30 | 0% |
| content | 0 | 1 | 0% |
| cube | 0 | 6 | 0% |
| friend | 0 | 7 | 0% |
| logs | 0 | 4 | 0% |
| member | 0 | 15 | 0% |
| notification | 0 | 4 | 0% |
| schedule | 1 | 7 | 14% |
| generaltodo | 0 | 17 | 0% |
| servertodo | 0 | 4 | 0% |
| domainMyGame | 0 | 10 | 0% |
| **총계** | **2** | **124** | **1.6%** |
