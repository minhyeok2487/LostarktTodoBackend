import { test, expect } from '@playwright/test';
import * as crypto from 'crypto';

const BASE = 'http://localhost:8081';
const JWT_SECRET_BASE64 = 'bG9jYWwtdGVzdC1zZWNyZXQta2V5LWZvci1tZW1vcnktb3B0aW1pemF0aW9uLXRlc3QtMTIzNDU2Nzg5MA==';
function createJWT(username: string): string {
  const key = Buffer.from(JWT_SECRET_BASE64, 'base64');
  const b64url = (obj: object) => Buffer.from(JSON.stringify(obj)).toString('base64url');
  const h = b64url({ alg: 'HS512', typ: 'JWT' });
  const p = b64url({ sub: username, iss: 'LostarkTodo', iat: Math.floor(Date.now() / 1000) });
  const sig = crypto.createHmac('sha512', key).update(`${h}.${p}`).digest('base64url');
  return `${h}.${p}.${sig}`;
}
const TOKEN = createJWT('qwe2487@ajou.ac.kr');
function auth() { return { Authorization: `Bearer ${TOKEN}` }; }

// ---------------------------------------------------------------------------
// Friend endpoints
// ---------------------------------------------------------------------------
test.describe('Friend endpoints', () => {

  // 1. GET /api/v1/friend -> 200, list friends
  test('GET /api/v1/friend returns 200 with friends array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/friend`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    // Each entry should have core friend fields
    for (const friend of body) {
      expect(friend).toHaveProperty('friendId');
      expect(friend).toHaveProperty('friendUsername');
      expect(friend).toHaveProperty('areWeFriend');
      expect(friend).toHaveProperty('ordering');
      expect(friend).toHaveProperty('toFriendSettings');
      expect(friend).toHaveProperty('fromFriendSettings');
    }
  });

  // 2. GET /api/v1/friend/character/{characterName} -> search
  test('GET /api/v1/friend/character/{name} searches for characters', async ({ request }) => {
    const res = await request.get(
      `${BASE}/api/v1/friend/character/${encodeURIComponent('테스트 계정 1')}`,
      { headers: auth() },
    );
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    // Results (if any) should carry search-result fields
    for (const item of body) {
      expect(item).toHaveProperty('id');
      expect(item).toHaveProperty('username');
      expect(item).toHaveProperty('characterName');
      expect(item).toHaveProperty('characterListSize');
      expect(item).toHaveProperty('areWeFriend');
    }
  });

  // 3. POST /api/v1/friend -> send friend request (idempotency-safe: may already exist)
  test('POST /api/v1/friend send friend request returns 200 or 400 if already exists', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/friend`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { friendUsername: 'test-friend-nonexistent@example.com' },
    });
    // Either 200 (success) or 400 (already friends / target not found)
    expect([200, 400]).toContain(res.status());
    expect(res.status()).not.toBe(500);
  });

  // 4. POST /api/v1/friend/request -> handle friend request (accept/reject/delete)
  test('POST /api/v1/friend/request handles friend action', async ({ request }) => {
    // Attempt to reject a non-existent friend request -- expect 400 (no such request)
    const res = await request.post(`${BASE}/api/v1/friend/request`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { friendUsername: 'nonexistent-user@example.com', category: 'REJECT' },
    });
    // Should be 400 because the target user doesn't exist
    expect([200, 400]).toContain(res.status());
    expect(res.status()).not.toBe(500);
  });

  // 5. PATCH /api/v1/friend/settings -> update friend settings
  test('PATCH /api/v1/friend/settings updates a setting field', async ({ request }) => {
    // First get friends to find a valid friendId
    const listRes = await request.get(`${BASE}/api/v1/friend`, { headers: auth() });
    const friends = await listRes.json();

    if (friends.length === 0) {
      test.skip();
      return;
    }

    const friendId = friends[0].friendId;
    const currentShowRaid = friends[0].toFriendSettings?.showRaid ?? true;

    // Toggle showRaid
    const res = await request.patch(`${BASE}/api/v1/friend/settings`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { id: friendId, name: 'showRaid', value: !currentShowRaid },
    });
    expect(res.status()).toBe(200);
    const settings = await res.json();
    expect(settings).toHaveProperty('showRaid', !currentShowRaid);

    // Restore original value
    const restoreRes = await request.patch(`${BASE}/api/v1/friend/settings`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { id: friendId, name: 'showRaid', value: currentShowRaid },
    });
    expect(restoreRes.status()).toBe(200);
  });

  // 6. PUT /api/v1/friend/sort -> update friend sort order
  test('PUT /api/v1/friend/sort updates sort order', async ({ request }) => {
    // Get current friends to collect IDs
    const listRes = await request.get(`${BASE}/api/v1/friend`, { headers: auth() });
    const friends = await listRes.json();

    if (friends.length === 0) {
      test.skip();
      return;
    }

    const friendIds = friends.map((f: any) => f.friendId);

    const res = await request.put(`${BASE}/api/v1/friend/sort`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { friendIdList: friendIds },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('message', 'ok');
  });
});

// ---------------------------------------------------------------------------
// Notification endpoints
// ---------------------------------------------------------------------------
test.describe('Notification endpoints', () => {

  // 7. GET /api/v1/notification -> 200, array
  test('GET /api/v1/notification returns 200 with array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/notification`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    for (const n of body) {
      expect(n).toHaveProperty('notificationId');
      expect(n).toHaveProperty('content');
      expect(n).toHaveProperty('isRead');
      expect(n).toHaveProperty('createdAt');
    }
  });

  // 8. GET /api/v1/notification/status -> 200, recent notification status
  test('GET /api/v1/notification/status returns 200 with status', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/notification/status`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('hasNew');
    expect(typeof body.hasNew).toBe('boolean');
  });

  // 9. POST /api/v1/notification/{notificationId} -> mark as read
  test('POST /api/v1/notification/{id} marks notification as read', async ({ request }) => {
    // Try with notification id 78
    const res = await request.post(`${BASE}/api/v1/notification/78`, {
      headers: auth(),
    });
    // 200 if found & owned, 400 if not found
    expect([200, 400]).toContain(res.status());
    expect(res.status()).not.toBe(500);
  });

  // 10. POST /api/v1/notification/all -> mark all as read
  test('POST /api/v1/notification/all marks all as read', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/notification/all`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('message');
  });
});

// ---------------------------------------------------------------------------
// Follow endpoints
// ---------------------------------------------------------------------------
test.describe('Follow endpoints', () => {

  // 11. GET /api/v1/follow -> 200, list followers
  test('GET /api/v1/follow returns 200 with array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/follow`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    for (const f of body) {
      expect(f).toHaveProperty('followId');
      expect(f).toHaveProperty('memberId');
      expect(f).toHaveProperty('followingId');
      expect(f).toHaveProperty('followName');
      expect(f).toHaveProperty('createdAt');
    }
  });

  // 12. POST /api/v1/follow -> toggle follow (idempotent toggle)
  test('POST /api/v1/follow toggles follow and back', async ({ request }) => {
    // Toggle follow on a known test user -- if user not found, expect 400
    const res1 = await request.post(`${BASE}/api/v1/follow`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { followingUsername: 'test-follow-target@example.com' },
    });
    // 200 if target exists, 400 if not
    expect([200, 400]).toContain(res1.status());
    expect(res1.status()).not.toBe(500);

    if (res1.status() === 200) {
      const body1 = await res1.json();
      expect(body1).toHaveProperty('following');

      // Toggle back to restore state
      const res2 = await request.post(`${BASE}/api/v1/follow`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: { followingUsername: 'test-follow-target@example.com' },
      });
      expect(res2.status()).toBe(200);
      const body2 = await res2.json();
      expect(body2).toHaveProperty('following');
      // The toggle should have reversed
      expect(body2.following).not.toBe(body1.following);
    }
  });
});

// ---------------------------------------------------------------------------
// Community CRUD
// ---------------------------------------------------------------------------
test.describe('Community endpoints', () => {

  // 13. GET /api/v1/community/category -> 200, Korean categories
  test('GET /api/v1/community/category returns Korean categories', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/community/category`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    expect(body.length).toBeGreaterThanOrEqual(1);
    // Known categories from handler source
    const expectedCategories = ['일상', '깐부모집', '길드모집', '고정팟모집', '로투두공지', '로투두건의사항'];
    expect(body).toEqual(expectedCategories);
  });

  // 14. GET /api/v1/community?page=0&size=5 -> 200, posts
  test('GET /api/v1/community returns paginated posts', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/community?page=0&size=5`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('posts');
    expect(body).toHaveProperty('totalCount');
    expect(body).toHaveProperty('page', 0);
    expect(body).toHaveProperty('size', 5);
    expect(Array.isArray(body.posts)).toBe(true);
    for (const post of body.posts) {
      expect(post).toHaveProperty('communityId');
      expect(post).toHaveProperty('category');
      expect(post).toHaveProperty('title');
      expect(post).toHaveProperty('body');
      expect(post).toHaveProperty('username');
      expect(post).toHaveProperty('likeCount');
      expect(post).toHaveProperty('commentCount');
      expect(typeof post.liked).toBe('boolean');
    }
  });

  // 15. Community CRUD: create -> get -> delete -> verify deleted
  test('POST, GET, DELETE /api/v1/community full CRUD lifecycle', async ({ request }) => {
    // CREATE
    const createRes = await request.post(`${BASE}/api/v1/community`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        category: '일상',
        title: '[Playwright Test] 자동 테스트 게시글',
        body: '이것은 Playwright 자동 테스트로 생성된 게시글입니다. 곧 삭제됩니다.',
      },
    });
    expect(createRes.status()).toBe(201);
    const created = await createRes.json();
    expect(created).toHaveProperty('communityId');
    expect(created.category).toBe('일상');
    expect(created.title).toBe('[Playwright Test] 자동 테스트 게시글');
    expect(created.body).toContain('Playwright 자동 테스트');
    const communityId = created.communityId;

    // READ back
    const getRes = await request.get(`${BASE}/api/v1/community/${communityId}`, {
      headers: auth(),
    });
    expect(getRes.status()).toBe(200);
    const fetched = await getRes.json();
    expect(fetched.communityId).toBe(communityId);
    expect(fetched.title).toBe('[Playwright Test] 자동 테스트 게시글');

    // DELETE (soft delete)
    const deleteRes = await request.delete(`${BASE}/api/v1/community/${communityId}`, {
      headers: auth(),
    });
    expect(deleteRes.status()).toBe(200);
    const deleteBody = await deleteRes.json();
    expect(deleteBody).toHaveProperty('message', 'post deleted');

    // VERIFY deleted: GET should now return 400 or 404 (post marked deleted)
    const verifyRes = await request.get(`${BASE}/api/v1/community/${communityId}`, {
      headers: auth(),
    });
    expect([400, 404]).toContain(verifyRes.status());
  });

  // 16. POST /api/v1/community/like/{communityId} -> toggle like
  test('POST /api/v1/community/like/{id} toggles like', async ({ request }) => {
    const communityId = 236;
    const res1 = await request.post(`${BASE}/api/v1/community/like/${communityId}`, {
      headers: auth(),
    });
    // 200 if post exists, 400 if not
    expect([200, 400]).toContain(res1.status());
    expect(res1.status()).not.toBe(500);

    if (res1.status() === 200) {
      const body1 = await res1.json();
      expect(body1).toHaveProperty('liked');
      expect(typeof body1.liked).toBe('boolean');

      // Toggle back to restore state
      const res2 = await request.post(`${BASE}/api/v1/community/like/${communityId}`, {
        headers: auth(),
      });
      expect(res2.status()).toBe(200);
      const body2 = await res2.json();
      expect(body2.liked).not.toBe(body1.liked);
    }
  });
});

// ---------------------------------------------------------------------------
// Comments CRUD
// ---------------------------------------------------------------------------
test.describe('Comments endpoints', () => {

  // 17. GET /api/v1/comments?communityId=236 -> 200, comments array
  test('GET /api/v1/comments returns comments array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/comments?communityId=236`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    for (const comment of body) {
      expect(comment).toHaveProperty('commentId');
      expect(comment).toHaveProperty('communityId');
      expect(comment).toHaveProperty('username');
      expect(comment).toHaveProperty('body');
      expect(comment).toHaveProperty('children');
      expect(Array.isArray(comment.children)).toBe(true);
    }
  });

  // Validation: missing communityId should return 400
  test('GET /api/v1/comments without communityId returns 400', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/comments`, {
      headers: auth(),
    });
    expect(res.status()).toBe(400);
  });

  // 18. Comments CRUD: create -> update -> delete -> verify
  test('POST, PATCH, DELETE /api/v1/comments full CRUD lifecycle', async ({ request }) => {
    // First, create a community post to comment on (so we don't pollute existing data)
    const postRes = await request.post(`${BASE}/api/v1/community`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        category: '일상',
        title: '[Playwright Test] 댓글 테스트용 게시글',
        body: '댓글 CRUD 테스트를 위한 게시글',
      },
    });
    expect(postRes.status()).toBe(201);
    const post = await postRes.json();
    const communityId = post.communityId;

    // CREATE comment
    const createRes = await request.post(`${BASE}/api/v1/comments`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        communityId: communityId,
        body: '[Playwright Test] 자동 생성 댓글입니다.',
      },
    });
    expect(createRes.status()).toBe(201);
    const created = await createRes.json();
    expect(created).toHaveProperty('commentId');
    expect(created.body).toBe('[Playwright Test] 자동 생성 댓글입니다.');
    expect(created.communityId).toBe(communityId);
    const commentId = created.commentId;

    // UPDATE comment
    const updateRes = await request.patch(`${BASE}/api/v1/comments/${commentId}`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { body: '[Playwright Test] 수정된 댓글입니다.' },
    });
    expect(updateRes.status()).toBe(200);
    const updateBody = await updateRes.json();
    expect(updateBody).toHaveProperty('message', 'comment updated');

    // DELETE comment
    const deleteRes = await request.delete(`${BASE}/api/v1/comments/${commentId}`, {
      headers: auth(),
    });
    expect(deleteRes.status()).toBe(200);
    const deleteBody = await deleteRes.json();
    expect(deleteBody).toHaveProperty('message', 'comment deleted');

    // VERIFY deleted: list comments for this post should not contain our comment
    const listRes = await request.get(`${BASE}/api/v1/comments?communityId=${communityId}`, {
      headers: auth(),
    });
    expect(listRes.status()).toBe(200);
    const comments = await listRes.json();
    const found = comments.find((c: any) => c.commentId === commentId);
    expect(found).toBeUndefined();

    // Clean up: delete the community post
    await request.delete(`${BASE}/api/v1/community/${communityId}`, { headers: auth() });
  });
});

// ---------------------------------------------------------------------------
// Schedule CRUD
// ---------------------------------------------------------------------------
test.describe('Schedule endpoints', () => {

  // 19. GET /api/v1/schedule -> 200, array
  test('GET /api/v1/schedule returns 200 with array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/schedule`, { headers: auth() });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    for (const s of body) {
      expect(s).toHaveProperty('scheduleId');
      expect(s).toHaveProperty('scheduleCategory');
      expect(s).toHaveProperty('scheduleRaidCategory');
      expect(s).toHaveProperty('dayOfWeek');
    }
  });

  // 20. GET /api/v1/schedule/raid/category -> 200, raid categories
  test('GET /api/v1/schedule/raid/category returns raid categories', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/schedule/raid/category`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    for (const cat of body) {
      expect(cat).toHaveProperty('name');
      expect(cat).toHaveProperty('raidNames');
      expect(Array.isArray(cat.raidNames)).toBe(true);
    }
  });

  // 21. Schedule CRUD: create -> update -> delete -> verify
  test('POST, PATCH, DELETE /api/v1/schedule full CRUD lifecycle', async ({ request }) => {
    // CREATE
    const createRes = await request.post(`${BASE}/api/v1/schedule`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        scheduleCategory: 'RAID',
        scheduleRaidCategory: '에키드나',
        raidName: '에키드나 하드',
        dayOfWeek: 3,
        timeSlot: '21:00',
        memo: '[Playwright Test] 자동 생성 일정',
      },
    });
    expect(createRes.status()).toBe(201);
    const created = await createRes.json();
    expect(created).toHaveProperty('scheduleId');
    expect(created.scheduleCategory).toBe('RAID');
    expect(created.scheduleRaidCategory).toBe('에키드나');
    expect(created.raidName).toBe('에키드나 하드');
    expect(created.dayOfWeek).toBe(3);
    expect(created.timeSlot).toBe('21:00');
    expect(created.memo).toBe('[Playwright Test] 자동 생성 일정');
    const scheduleId = created.scheduleId;

    // UPDATE
    const updateRes = await request.patch(`${BASE}/api/v1/schedule/${scheduleId}`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        scheduleCategory: 'RAID',
        scheduleRaidCategory: '에키드나',
        raidName: '에키드나 노말',
        dayOfWeek: 4,
        timeSlot: '22:00',
        memo: '[Playwright Test] 수정된 일정',
      },
    });
    expect(updateRes.status()).toBe(200);
    const updateBody = await updateRes.json();
    expect(updateBody).toHaveProperty('message', 'schedule updated');

    // DELETE
    const deleteRes = await request.delete(`${BASE}/api/v1/schedule/${scheduleId}`, {
      headers: auth(),
    });
    expect(deleteRes.status()).toBe(200);
    const deleteBody = await deleteRes.json();
    expect(deleteBody).toHaveProperty('message', 'schedule deleted');

    // VERIFY deleted: try deleting again, should get 400
    const verifyRes = await request.delete(`${BASE}/api/v1/schedule/${scheduleId}`, {
      headers: auth(),
    });
    expect(verifyRes.status()).toBe(400);
  });
});
