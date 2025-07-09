# 로스트아크 일정 관리 사이트 "로아투두"
### [https://app.loatodo.com](https://app.loatodo.com)
![img_1.png](img_1.png)

## 프로젝트 소개
로스트아크 유저들을 위한 일정 관리 및 커뮤니티 웹사이트입니다.

로스트아크 오픈 API와 연동하여 캐릭터 정보를 가져오고,

일일/주간 숙제 관리, 친구(깐부) 기능, 캘린더, 게시판 등 다양한 편의 기능을 제공합니다.

## 주요 기능
- **캐릭터 데이터 연동**: 로스트아크 오픈 API를 통해 캐릭터의 데이터를 실시간으로 호출합니다.
- **숙제(일정) 관리**:
    - **일일/주간 컨텐츠 관리**: 카오스 던전, 가디언 토벌 등 일일/주간 숙제를 체크하고 휴식 게이지를 자동으로 계산합니다.
    - **예상 수익 계산**: 거래소 및 경매장 데이터를 기반으로 컨텐츠 완료 시 예상 수익을 보여줍니다.
    - **초기화**: 매일 오전 6시(일일), 매주 수요일 오전 6시(주간)에 컨텐츠가 자동으로 초기화됩니다.
    - **커스텀 메뉴**: 사용자가 원하는 항목을 직접 추가하여 관리할 수 있습니다.
- **깐부(친구) 시스템**: 다른 사용자와 숙제 현황을 공유하고, 각 항목별로 권한을 설정할 수 있습니다.
- **일정 관리**: 캘린더 형식으로 개인적인 일정을 등록하고 관리합니다.
- **커뮤니티**:
    - **방명록**: 다른 유저와 간단한 메시지를 주고받을 수 있습니다.
    - **모집 게시판**: 깐부, 길드, 공격대를 모집하고 홍보할 수 있는 게시판입니다.
- **알림**: 공지사항, 방명록 댓글, 깐부 요청 등에 대한 실시간 알림을 제공합니다.
- **유틸리티**:
    - **큐브 계산기**: 실버 큐브, 엘리트 큐브 등의 통계를 바탕으로 예상 수익을 계산합니다.

## 기술 스택
- **Programming Languages**: Java, TypeScript, HTML5, CSS3
- **Framework**: SpringBoot, React
- **Library**: JPA, QueryDSL, Spring Security, Spring Cache, Swagger, JWT, Recoil, React-Query
- **Tooling / DevOps**: Docker, Git
- **Environment**: AWS (EC2, RDS, S3, ECR, ECS, CodePipeline, ElastiCache)
- **Database**: MySQL
- **Collaboration**: Jira, Confluence, Discord

## 인력 구성
- **PM, 백엔드 개발**: 1명 (본인)
- **UI/UX 기획, 퍼블리셔**: 1명
- **프론트엔드 개발**: 1명

## API Documentation (Swagger)
- **[https://api2.loatodo.com/swagger-ui.html](https://api2.loatodo.com/swagger-ui.html)**

## Database Schema
![image](https://github.com/user-attachments/assets/674b1dee-1996-4d4e-9c34-ee1257aae23b)

## 개발 및 운영 기록
- **2023.07.02 ~ 2023.09.10**: 기본 기능 개발
- **2023.09.11**: 베타 버전 배포
- **2023.10.10**: 메인 기능(숙제 관리) 배포
- **2023.12.01**: 친구 기능 추가
- **2024.01.10**: 이메일 인증 로그인 구현
- **2024.02.21**: 홈 화면 리빌딩
- **2024.06.01**: 프론트엔드 재구축 (TypeScript, React Query, Recoil)
- **2024.06.16**: 백엔드 서버 RI 및 CI/CD 환경 구성
- **2024.07.10**: 알림 기능 추가
- **2024.10.13**: 큐브 계산기 기능 추가
- **2024.11.21**: 커뮤니티 기능 추가
- **2024.02.20**: 숙제 로그(수익) 조회 기능 추가(베타)
- **2025.04.11**: 일정 자동 체크 기능 추가
- **2025.05.26**: 생활의 기운 관리 기능 추가
- **2025.06.26**: 캐릭터 전투력 및 귀속 골드 정보 추가
- **2025.06.30**: 숙제 로그(수익) 기능 개선

## Github Repository
- **[Backend](https://github.com/minhyeok2487/LostarktTodoBackend)**
- **[Frontend](https://github.com/minhyeok2487/LoatodoFrontWithTs)**

## 참고 사이트
* **[로스트아크 오픈 API](https://developer-lostark.game.onstove.com/getting-started)**
* **[빈아크](https://ark.bynn.kr/to-do)**
* **[클로아(디자인 참고)](https://kloa.gg)**

---

## 회고
- **2024.06.16 / 백엔드서버 RI 및 CI/CD 환경 구성**
  - 기존 ElasticBeanstalk으로 운영하던 비용이 부담되어 서버비용 감축을 위함
  - AWS Code Build, Code Pipeline, ECR, ECS 사용
  - 예약 인스턴스(RI)를 이용하여 서버비용 감축
  - 블로그 정리:
    - [CI/CD 구축기 1](https://repeater2487.tistory.com/193)
    - [CI/CD 구축기 2](https://repeater2487.tistory.com/196)
    - [CI/CD 구축기 3](https://repeater2487.tistory.com/197)
- **2024.06.01 / 프론트엔트 환경 재구축**
  - `useState`만으로 상태를 관리하는 것에 대한 유지보수 어려움을 느낌
  - TypeScript, React Query, Recoil을 도입하여 상태 관리 개선 및 기능 확장 용이성 확보
- **2024.02.26 / 전략패턴을 이용한 코드 리팩토링**
  - `if`문을 사용하던 기존 정책 분리 로직을 `Enum`과 전략 패턴을 이용하여 리팩토링
  - 새로운 정책 추가 시 유연하게 대처할 수 있는 구조로 개선
  - 관련 포스트: [전략패턴 리팩토링](https://repeater2487.tistory.com/183)
- **2023.12.31 ~ 2024.02.01 / Redis에 관하여**
  - 초기 이메일 인증 로직에 Redis(AWS ElastiCache)를 사용했으나, 비용 문제로 RDBMS로 변경
  - 관련 포스트: [Redis 도입 및 제거 과정](https://repeater2487.tistory.com/182)
- **2023.11.24 / N+1 문제 해결**
  - 1:N 관계 테이블 조회 시 발생하던 N+1 문제를 JPQL `JOIN FETCH`를 사용하여 해결
  - `LEFT JOIN`을 활용하여 데이터가 없는 경우에도 누락되지 않도록 처리
  - 관련 포스트: [N+1 문제 해결기](https://repeater2487.tistory.com/164)
- **2023.11.08 / 메모리 부족으로 인한 서버 다운**
  - 대량 데이터 업데이트 시 JPA 더티 체킹으로 인한 메모리 부족 문제 발생
  - JPQL을 이용한 Bulk Update로 변경하여 문제 해결
  - 관련 포스트: [JPA 더티 체킹과 메모리 문제](https://repeater2487.tistory.com/163)