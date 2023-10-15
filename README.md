# 로스트아크 숙제 체크 사이트
## https://app.loatodo.com
![img_1.png](resource/main.png)

## Github
- [Backend](https://github.com/minhyeok2487/LostarktTodoBackend)
- [Frontend](https://github.com/minhyeok2487/LostarkTodoFrontend)

## 프로젝트
### 개요
- 로스트아크 일일, 주간 컨텐츠 관리 사이트
    - 일일 컨텐츠 : 카오스 던전, 가디언 토벌, 에포나 의뢰
        - 로스트아크 오픈 API의 거래소, 경매장 등의 데이터를 호출해 예상 수익 계산
        - 체크한 내용을 바탕으로 휴식게이지 계산 -> 인게임과 동일한 휴식게이지
    - 주간 컨텐츠 : 군단장 레이드, 어비스 던전, 어비스레이드
        - 주간 수익 계산
        - 몇 개의 2주 주기 컨텐츠 계산
- 컨텐츠 완료 한 기록 저장 -> 달력식으로 보기
- 주간숙제 고정팟 관리 -> 접속시간 기록 바탕으로 고정팟 추천

### 인원
* 개발자 1 (백엔드 & 프론트) PM
* 퍼블리셔 1 (2023.10.12~)

### 기간
* 2023.08.02 ~ 2023.09.10(기본 기능 개발)
* 2023.09.11 베타 버전 배포
* 2023.10.10 메인 기능(숙제 관리) 배포 완료

### 배포기록
* https://www.inven.co.kr/board/lostark/4821?name=nicname&keyword=Dev&eq=1&iskin=


### 주요 기능
#### 1. 가입
##### **구글 로그인으로 가입 가능**
* ApiKey, 대표캐릭터 -> ApiKey 가 필수이기 때문에 로스트아크 서버 점검 중에는 가입불가
![00.gif](resource/00.gif)

#### 2. Todo
##### **캐릭터 Info**
  * 캐릭터 이미지 안에 클래스, 서버, 아이템 레벨, 캐릭터 이름 출력
  * 원하는 컨텐츠만 출력
  ![09.gif](resource/09.gif)


  * 캐릭터 순서 변경
  ![12.gif](resource/12.gif)



##### **일일 숙제 체크**
  * 돋보기 클릭 시 재료 통계 출력
  ![01.gif](resource/01.gif)


  * 하단 녹색 바 클릭 시 휴식게이지 수정 -> 자동으로 예상수익 변경
  ![02.gif](resource/02.gif)


  * 매일 오전 6시 일일숙제 초기화 -> 체크된 내용으로 휴식게이지 계산 -> 예상수익 변경


##### **주간 숙제 관리**
  * 원하는 컨텐츠 체크 후 저장
  ![03.gif](resource/03.gif)  


  * 같은 레이드 동시 선택 불가 (예시 - 카양겔(노말), 카양겔(하드))
  ![04.gif](resource/04.gif)


  * 골드 획득이 높은 순으로 정렬 -> 3개 주간 골드로 포함
  ![05.gif](resource/05.gif)


* 서버별 출력 분리
![06.gif](resource/06.gif)

* 주간 원정대 숙제 체크 -> 서버별
  * 도전 어비스 던전
  * 도전 가디언 토벌
![07.gif](resource/07.gif)


* 골드 획득 캐릭터 지정
* 수익 / 예상 수익
  * 일일 수익 / 예상 일일 수익
  * 주간 수익 / 예상 주간 수익 -> 단, 골드 획득 지정 캐릭터만
![08.gif](resource/08.gif)


#### 기타
##### **다크모드**
![10.gif](resource/10.gif)


##### **방명록**
![11.gif](resource/11.gif)

### 참고 사이트
* [로스트아크 오픈 API](https://developer-lostark.game.onstove.com/getting-started)
* [빈아크](https://ark.bynn.kr/to-do)
* [클로아(디자인 참고)](https://kloa.gg)




## STACKS
### Environment
<div>
  <img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
</div>

### Backend Development
<div>
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
</div>


### Frontend Development
<div>
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white">
</div>

### DataBase
<div>
  <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/mySql-003545?style=for-the-badge&logo=MySql&logoColor=white">
</div>

### Communication
<div>
<img src="https://img.shields.io/badge/confluence-172B4D?style=for-the-badge&logo=confluence&logoColor=white">
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</div>

## 문서 정리
### Backend 프로젝트 진행 중 성장
* [JPA N+1 문제](https://repeater2487.tistory.com/129)
* [JPQL 사용](https://repeater2487.tistory.com/130)
* [엔티티 생성/수정 시각 자동화 Auditing](https://repeater2487.tistory.com/131)
* [API 문서 자동화 Swagger](https://repeater2487.tistory.com/134)
* [빌더 패턴 (with Lombok)](https://repeater2487.tistory.com/135)
* [일대다 매핑과 빌더 패턴](https://repeater2487.tistory.com/136)
* [int와 Integer, boolean과 Boolean](https://repeater2487.tistory.com/137)
