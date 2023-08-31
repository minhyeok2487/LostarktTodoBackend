# 로스트아크 숙제 체크 사이트
- [Backend](https://github.com/minhyeok2487/LostarkTodo)
- [Frontend](https://github.com/minhyeok2487/lostarkTodoReact)

---
## 프로젝트
### 개요
- 로스트아크 일일, 주간 컨텐츠 체크 및 공유 사이트
    - 일일 컨텐츠 : 카오스 던전, 가디언 토벌, 에포나 의뢰
        - 로스트아크 오픈 API의 거래소, 경매장 등의 데이터를 호출해 예상 수익 계산
    - 주간 컨텐츠 : 군단장 레이드, 어비스 던전, 어비스레이드
        - 다른 사용자와 공유
- 컨텐츠 완료 한 기록 저장

### 기간
* 2023.08.02 ~ 2023.08.31(기본 기능 개발)
* 2023.09.01 ~ 2023.09.15(베타 버전 배포)

### 주요 기능
#### 1. 회원 가입 시 검증
* 이메일 인증 
* 비밀번호 확인 
* ApiKey, 대표캐릭터 -> Api가 필수이기 때문에 로아 점검 중에는 가입불가

#### 2. 로그인, 구글 계정 연동
* ApiKey, 대표캐릭터만 추가

#### 3. Todo
* 캐릭터 Info
  * 캐릭터 이미지 안에 클래스, 서버, 아이템 레벨, 캐릭터 이름 출력
* 일일 숙제 체크
  * 돋보기 클릭 시 재료 통계와 시세 보기
  * 체크 버튼 오른쪽 클릭 시 휴식게이지 수정
  * 매일 오전 6시 초기화(휴식게이지 계산)
* 주간 숙제 관리
  * 원하는 컨텐츠 체크 후 저장
  * 같은 레이드 동시 선택 불가 (예시 - 카양겔(노말), 카양겔(하드))
  * 최대 3개
* 주간 원정대 숙제 체크
  * 도전 어비스 던전
  * 도전 가디언 토벌
* 수익 / 예상 수익
  * 일일 수익 / 예상 일일 수익
  * 주간 수익 / 예상 주간 수익 -> 단, 골드 획득 지정 캐릭터만
* 골드 획득 캐릭터 지정

### 참고 사이트
* [로스트아크 오픈 API](ttps://developer-lostark.game.onstove.com/getting-started)
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
<div>
  <img src="https://img.shields.io/badge/Intellij-000000?style=for-the-badge&logo=IntelliJ%20IDEA&logoColor=white">
  <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white">
</div>

### Frontend Development
<div>
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white">
</div>
<div>
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=Thymeleaf&logoColor=white">
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white">
</div>

### DataBase
<div>
  <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white">
</div>

### Communication
<div>
  <img src="https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=jira&logoColor=white">
<img src="https://img.shields.io/badge/confluence-172B4D?style=for-the-badge&logo=confluence&logoColor=white">
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</div>

---
## 문서 정리
### Backend 프로젝트 진행 중 성장
* [JPA N+1 문제](https://repeater2487.tistory.com/129)
* [JPQL 사용](https://repeater2487.tistory.com/130)
* [엔티티 생성/수정 시각 자동화 Auditing](https://repeater2487.tistory.com/131)
* [API 문서 자동화 Swagger](https://repeater2487.tistory.com/134)
* [빌더 패턴 (with Lombok)](https://repeater2487.tistory.com/135)
* [일대다 매핑과 빌더 패턴](https://repeater2487.tistory.com/136)
* [int와 Integer, boolean과 Boolean](https://repeater2487.tistory.com/137)
