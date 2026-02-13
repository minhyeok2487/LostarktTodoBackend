import { test, expect } from '@playwright/test';
import * as crypto from 'crypto';

const BASE_URL = 'http://localhost:8081';
const JWT_SECRET_BASE64 = 'bG9jYWwtdGVzdC1zZWNyZXQta2V5LWZvci1tZW1vcnktb3B0aW1pemF0aW9uLXRlc3QtMTIzNDU2Nzg5MA==';
const TEST_USERNAME = 'playwright-test-' + Date.now() + '@test.com';
const TEST_PASSWORD = 'test1234!';

/** Generate HS512 JWT matching Go server's token format */
function createJWT(username: string): string {
  const key = Buffer.from(JWT_SECRET_BASE64, 'base64');

  const header = { alg: 'HS512', typ: 'JWT' };
  const payload = {
    sub: username,
    iss: 'LostarkTodo',
    iat: Math.floor(Date.now() / 1000),
  };

  const b64url = (obj: object) =>
    Buffer.from(JSON.stringify(obj)).toString('base64url');

  const headerB64 = b64url(header);
  const payloadB64 = b64url(payload);
  const signature = crypto
    .createHmac('sha512', key)
    .update(`${headerB64}.${payloadB64}`)
    .digest('base64url');

  return `${headerB64}.${payloadB64}.${signature}`;
}

// We'll store the auth token after signup/login
let authToken = '';
let testMemberUsername = '';

// ============================================================
// Health & Root
// ============================================================

test.describe('Health & Root', () => {
  test('1. GET /manage/health → 200, status UP', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/manage/health`);
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.status).toBe('UP');
  });

  test('2. GET / → 200, ok', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/`);
    expect(res.status()).toBe(200);
    const text = await res.text();
    expect(text).toBe('ok');
  });

  test('3. GET /manage/info → 200, app info', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/manage/info`);
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.app).toBe('lostark-todo-backend');
    expect(body.lang).toBe('go');
  });
});

// ============================================================
// Auth: Signup + Login
// ============================================================

test.describe.serial('Auth', () => {
  test('4. POST /api/v1/auth/signup → 200, token issued', async ({ request }) => {
    const res = await request.post(`${BASE_URL}/api/v1/auth/signup`, {
      data: { username: TEST_USERNAME, password: TEST_PASSWORD },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.token).toBeTruthy();
    expect(body.username).toBe(TEST_USERNAME);
    authToken = body.token;
    testMemberUsername = body.username;
  });

  test('5. POST /api/v1/auth/login → 200, token issued', async ({ request }) => {
    const res = await request.post(`${BASE_URL}/api/v1/auth/login`, {
      data: { username: TEST_USERNAME, password: TEST_PASSWORD },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.token).toBeTruthy();
    authToken = body.token; // refresh token
  });

  test('5b. POST /api/v1/auth/signup duplicate → 400', async ({ request }) => {
    const res = await request.post(`${BASE_URL}/api/v1/auth/signup`, {
      data: { username: TEST_USERNAME, password: TEST_PASSWORD },
    });
    expect(res.status()).toBe(400);
  });
});

// ============================================================
// Authenticated endpoints using existing test account
// ============================================================

// Use JWT for the existing test account qwe2487@ajou.ac.kr
const EXISTING_TOKEN = createJWT('qwe2487@ajou.ac.kr');

function authHeaders() {
  return { Authorization: `Bearer ${EXISTING_TOKEN}` };
}

function testAuthHeaders() {
  return { Authorization: `Bearer ${authToken}` };
}

test.describe('Member & Character (authenticated)', () => {
  test('6. GET /api/v1/member → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/member/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toBeTruthy();
  });

  test('7. GET /api/v1/character-list → 200, array', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/character-list/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
  });

  test('8. GET /api/v1/character-list/deleted → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/character-list/deleted`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// Content (some permit-all)
// ============================================================

test.describe('Content', () => {
  test('9. GET /api/v1/cube/statistics → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/cube/statistics`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('10. GET /api/v1/content/week/raid/category → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/content/week/raid/category`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// Social
// ============================================================

test.describe('Social', () => {
  test('11. GET /api/v1/friend → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/friend/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('12. GET /api/v1/notification → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/notification/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('13. GET /api/v1/notification/status → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/notification/status`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('14. GET /api/v1/follow → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/follow/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// Community
// ============================================================

test.describe('Community', () => {
  test('15. GET /api/v1/community/category → 200, array', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/community/category`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    expect(body).toContain('ALL');
  });

  test('16. GET /api/v1/community?page=0&size=5 → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/community/?page=0&size=5`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('17. GET /api/v1/comments → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/comments/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// Todos & Logs
// ============================================================

test.describe('Todos & Logs', () => {
  test('18. GET /api/v1/general-todos → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/general-todos/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('19. GET /api/v1/server-todos → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/server-todos/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('20. GET /api/v1/logs → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/logs/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('21. GET /api/v1/logs/profit → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/logs/profit`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('22. GET /api/v1/schedule → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/schedule/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('23. GET /api/v1/analysis → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/analysis/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });

  test('24. GET /api/v1/custom → 200', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/custom/`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// CRUD Round-trips
// ============================================================

test.describe.serial('CRUD: Community post', () => {
  let createdPostId: number;

  test('25. POST /api/v1/community → 201 (create post)', async ({ request }) => {
    const res = await request.post(`${BASE_URL}/api/v1/community/`, {
      headers: authHeaders(),
      data: {
        category: 'COMMUNITY',
        title: '[Playwright Test] 자동 테스트 게시글',
        body: 'This post was created by Playwright API test. Will be deleted.',
      },
    });
    expect(res.status()).toBe(201);
    const body = await res.json();
    expect(body.id || body.communityId).toBeTruthy();
    createdPostId = body.id || body.communityId;
  });

  test('26. DELETE /api/v1/community/{id} → 200', async ({ request }) => {
    expect(createdPostId).toBeTruthy();
    const res = await request.delete(`${BASE_URL}/api/v1/community/${createdPostId}`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

test.describe.serial('CRUD: General todo folder', () => {
  let createdFolderId: number;

  test('27. POST /api/v1/general-todos/folders → 201 (create folder)', async ({ request }) => {
    const res = await request.post(`${BASE_URL}/api/v1/general-todos/folders`, {
      headers: authHeaders(),
      data: { name: 'Playwright Test Folder' },
    });
    expect(res.status()).toBe(201);
    const body = await res.json();
    expect(body.id || body.folderId).toBeTruthy();
    createdFolderId = body.id || body.folderId;
  });

  test('28. DELETE /api/v1/general-todos/folders/{id} → 200', async ({ request }) => {
    expect(createdFolderId).toBeTruthy();
    const res = await request.delete(`${BASE_URL}/api/v1/general-todos/folders/${createdFolderId}`, {
      headers: authHeaders(),
    });
    expect(res.status()).toBe(200);
  });
});

// ============================================================
// Auth: No token → 403
// ============================================================

test.describe('Auth enforcement', () => {
  test('29. GET /api/v1/member without token → 403', async ({ request }) => {
    const res = await request.get(`${BASE_URL}/api/v1/member/`);
    expect(res.status()).toBe(403);
  });
});
