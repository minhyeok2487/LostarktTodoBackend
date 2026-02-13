/**
 * Spring Boot (8080) vs Go (8081) 응답 비교 테스트
 *
 * 목적: 같은 요청을 양쪽에 보내서 응답 차이를 문서화
 * - 공개 엔드포인트: 인증 없이 양쪽 비교
 * - 인증 엔드포인트: Go만 (JWT 키가 달라 Spring Boot 토큰 생성 불가)
 *
 * NOTE: Spring Boot는 프로덕션 JWT 키(AWS Parameter Store)로 실행 중.
 *       Go는 로컬 테스트 키로 실행 중. 같은 JWT로 양쪽 인증 불가.
 */
import { test, expect } from '@playwright/test';
import * as crypto from 'crypto';

const SPRING = 'http://localhost:8080';
const GO = 'http://localhost:8081';

// Go JWT 생성용
const JWT_SECRET_BASE64 = 'bG9jYWwtdGVzdC1zZWNyZXQta2V5LWZvci1tZW1vcnktb3B0aW1pemF0aW9uLXRlc3QtMTIzNDU2Nzg5MA==';
function createGoJWT(username: string): string {
  const key = Buffer.from(JWT_SECRET_BASE64, 'base64');
  const b64url = (obj: object) => Buffer.from(JSON.stringify(obj)).toString('base64url');
  const h = b64url({ alg: 'HS512', typ: 'JWT' });
  const p = b64url({ sub: username, iss: 'LostarkTodo', iat: Math.floor(Date.now() / 1000) });
  const sig = crypto.createHmac('sha512', key).update(`${h}.${p}`).digest('base64url');
  return `${h}.${p}.${sig}`;
}

const GO_TOKEN = createGoJWT('qwe2487@ajou.ac.kr');
function goAuth() {
  return { Authorization: `Bearer ${GO_TOKEN}` };
}

// ============================================================
// 1. Health & Root 비교
// ============================================================

test.describe('비교: Health & Root', () => {
  test('GET /manage/health → 양쪽 동일', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/manage/health`),
      request.get(`${GO}/manage/health`),
    ]);
    expect(spring.status()).toBe(200);
    expect(go.status()).toBe(200);

    const springBody = await spring.json();
    const goBody = await go.json();
    expect(springBody).toEqual({ status: 'UP' });
    expect(goBody).toEqual({ status: 'UP' });
  });

  test('GET / → Spring 200, Go 403 (permit-all 차이)', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/`),
      request.get(`${GO}/`),
    ]);
    // Spring Boot: "/" 접근 가능 (Spring Security permitAll)
    expect(spring.status()).toBe(200);
    const springText = await spring.text();
    expect(springText).toBe('ok');

    // Go: "/" 는 permitAllPaths에 없음 → 403
    expect(go.status()).toBe(403);
  });

  test('GET /manage/info → 응답 구조 다름', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/manage/info`),
      request.get(`${GO}/manage/info`),
    ]);
    expect(spring.status()).toBe(200);
    expect(go.status()).toBe(200);

    const springBody = await spring.json();
    const goBody = await go.json();

    // Spring Boot: build/java/os 정보 포함
    expect(springBody).toHaveProperty('build');
    expect(springBody).toHaveProperty('java');
    expect(springBody).toHaveProperty('os');

    // Go: 간단한 app/lang만
    expect(goBody).toEqual({ app: 'lostark-todo-backend', lang: 'go' });
  });
});

// ============================================================
// 2. 커뮤니티 카테고리 비교 (중요한 차이!)
// ============================================================

test.describe('비교: Community', () => {
  test('GET /api/v1/community/category → 카테고리 값 다름!', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/api/v1/community/category`),
      request.get(`${GO}/api/v1/community/category`),
    ]);
    expect(spring.status()).toBe(200);
    expect(go.status()).toBe(200);

    const springCats = await spring.json();
    const goCats = await go.json();

    // Spring Boot: 한글 카테고리명 (실제 서비스에서 사용하는 값)
    expect(springCats).toEqual(['일상', '깐부모집', '길드모집', '고정팟모집', '로투두공지', '로투두건의사항']);

    // Go: 영문 enum 이름 (BUG: Spring Boot의 한글 카테고리와 불일치)
    expect(goCats).toEqual(['ALL', 'COMMUNITY', 'SCREENSHOT', 'TIPS']);
  });

  test('GET /api/v1/community?page=0&size=2 → Spring 페이지 객체 vs Go SQL 에러', async ({ request }) => {
    const spring = await request.get(`${SPRING}/api/v1/community?page=0&size=2`);
    expect(spring.status()).toBe(200);
    const springBody = await spring.json();

    // Spring Boot: Spring Data 페이징 응답
    expect(springBody).toHaveProperty('content');
    expect(Array.isArray(springBody.content)).toBe(true);
    if (springBody.content.length > 0) {
      const post = springBody.content[0];
      expect(post).toHaveProperty('communityId');
      expect(post).toHaveProperty('body');
      expect(post).toHaveProperty('category');
      expect(post).toHaveProperty('likeCount');
      expect(post).toHaveProperty('commentCount');
    }

    // Go: SQL 에러 (SELECT id → member_id)
    const go = await request.get(`${GO}/api/v1/community/?page=0&size=2`);
    expect(go.status()).toBe(400);
    const goBody = await go.json();
    expect(goBody.message).toContain("Unknown column 'id'");
  });
});

// ============================================================
// 3. Auth 비교
// ============================================================

test.describe('비교: Auth', () => {
  test('POST /api/v1/auth/signup → 요청/응답 구조 다름', async ({ request }) => {
    // Spring Boot: mail, number(이메일인증), password, equalPassword 필요
    const springRes = await request.post(`${SPRING}/api/v1/auth/signup`, {
      data: { username: 'test@test.com', password: 'testtest1' },
    });
    expect(springRes.status()).toBe(400);
    const springErr = await springRes.json();
    // Spring Boot는 mail, number, equalPassword가 비어있어서 validation 에러
    expect(springErr).toHaveProperty('errorMessage');

    // Go: username, password만 필요 (단순 회원가입)
    const goUser = `compare-signup-${Date.now()}@test.com`;
    const goRes = await request.post(`${GO}/api/v1/auth/signup`, {
      data: { username: goUser, password: 'testtest1' },
    });
    expect(goRes.status()).toBe(200);
    const goBody = await goRes.json();
    expect(goBody).toHaveProperty('token');
    expect(goBody).toHaveProperty('username');
    expect(goBody).toHaveProperty('id');
  });

  test('POST /api/v1/auth/login → Spring은 정상, Go는 SQL 에러', async ({ request }) => {
    // Spring Boot: 비밀번호 모르는 계정이라 "가입하지 않은 회원" 에러지만 구조 확인
    const springRes = await request.post(`${SPRING}/api/v1/auth/login`, {
      data: { username: 'nonexistent@test.com', password: 'password123' },
    });
    expect(springRes.status()).toBe(400);
    const springErr = await springRes.json();
    // Spring Boot: { errorCode, exceptionName, errorMessage } 에러 구조
    expect(springErr).toHaveProperty('errorCode');
    expect(springErr).toHaveProperty('exceptionName');

    // Go: SQL 에러 (SELECT id → member_id)
    const goRes = await request.post(`${GO}/api/v1/auth/login`, {
      data: { username: 'nonexistent@test.com', password: 'password123' },
    });
    expect(goRes.status()).toBe(400);
    const goErr = await goRes.json();
    // Go: { message } 에러 구조
    expect(goErr).toHaveProperty('message');
    expect(goErr.message).toContain("Unknown column 'id'");
  });
});

// ============================================================
// 4. 에러 응답 구조 비교
// ============================================================

test.describe('비교: Error response format', () => {
  test('인증 실패 시 에러 형식 다름', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/api/v1/member`),
      request.get(`${GO}/api/v1/member/`),
    ]);
    expect(spring.status()).toBe(403);
    expect(go.status()).toBe(403);

    const springBody = await spring.json();
    const goBody = await go.json();

    // Spring Boot: { message: "..." } (단순 문자열, application/json)
    expect(springBody).toHaveProperty('message');

    // Go: { message: "..." } (동일한 형식)
    expect(goBody).toHaveProperty('message');

    // 메시지 내용은 동일
    expect(springBody.message).toContain('인증이 필요한 서비스');
    expect(goBody.message).toContain('인증이 필요한 서비스');
  });

  test('Spring Boot 비즈니스 에러는 errorCode/exceptionName 포함', async ({ request }) => {
    // Spring Boot: 비즈니스 로직 에러는 다른 형태
    const springRes = await request.post(`${SPRING}/api/v1/auth/login`, {
      data: { username: 'noone@test.com', password: 'pass1234' },
    });
    const springBody = await springRes.json();
    // Spring Boot 비즈니스 에러: { errorCode, exceptionName, errorMessage }
    expect(springBody).toHaveProperty('errorCode', 400);
    expect(springBody).toHaveProperty('exceptionName');
    expect(springBody).toHaveProperty('errorMessage');

    // Go: 모든 에러가 { message: "..." } 형식
    const goRes = await request.post(`${GO}/api/v1/auth/login`, {
      data: { username: 'noone@test.com', password: 'pass1234' },
    });
    const goBody = await goRes.json();
    expect(goBody).toHaveProperty('message');
    expect(Object.keys(goBody)).toEqual(['message']);
  });
});

// ============================================================
// 5. Go 인증 엔드포인트 (Spring Boot는 JWT 키 다름으로 비교 불가)
// ============================================================

test.describe('Go only: 인증 엔드포인트 (Spring은 JWT 키 달라 비교 불가)', () => {
  test('GET /api/v1/general-todos → Go 200 (정상)', async ({ request }) => {
    const res = await request.get(`${GO}/api/v1/general-todos/`, { headers: goAuth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('folders');
  });

  test('GET /api/v1/schedule → Go 200 (정상)', async ({ request }) => {
    const res = await request.get(`${GO}/api/v1/schedule/`, { headers: goAuth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
  });

  test('CRUD: general-todos folder 생성 → 삭제', async ({ request }) => {
    // Create
    const createRes = await request.post(`${GO}/api/v1/general-todos/folders`, {
      headers: goAuth(),
      data: { name: 'Spring-vs-Go 비교 테스트 폴더' },
    });
    expect(createRes.status()).toBe(201);
    const folder = await createRes.json();
    const folderId = folder.id || folder.folderId;
    expect(folderId).toBeTruthy();

    // Delete
    const deleteRes = await request.delete(`${GO}/api/v1/general-todos/folders/${folderId}`, {
      headers: goAuth(),
    });
    expect(deleteRes.status()).toBe(200);
  });
});

// ============================================================
// 6. permitAll 경로 차이 비교
// ============================================================

test.describe('비교: permitAll 경로 차이', () => {
  test('GET /api/v1/auth/logout → 양쪽 모두 403 (인증 필요)', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/api/v1/auth/logout`),
      request.get(`${GO}/api/v1/auth/logout`),
    ]);
    // 양쪽 동일 동작: 인증 없이 접근 시 403
    expect(spring.status()).toBe(403);
    expect(go.status()).toBe(403);
  });

  test('GET /api/v1/comments?communityId=1 → Spring 403, Go 200 (permitGet 차이)', async ({ request }) => {
    const [spring, go] = await Promise.all([
      request.get(`${SPRING}/api/v1/comments?communityId=1`),
      request.get(`${GO}/api/v1/comments/?communityId=1`, { headers: goAuth() }),
    ]);
    // Spring Boot: comments는 인증 필요
    expect(spring.status()).toBe(403);

    // Go: comments는 인증하면 접근 가능
    expect(go.status()).toBe(200);
  });
});
