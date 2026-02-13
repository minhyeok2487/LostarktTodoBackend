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

// Test account constants
const USERNAME = 'qwe2487@ajou.ac.kr';
const MEMBER_ID = 365;
const CHARACTER_ID_1 = 114672; // 테스트 계정 4, 루페온, item_level 1700
const CHARACTER_ID_2 = 114669; // 테스트 계정 1, 루페온, item_level 1706.67
const SERVER_NAME = '루페온';

// ============================================================================
// MEMBER ENDPOINTS
// ============================================================================
test.describe('Member endpoints', () => {

  // 1. GET /api/v1/member -> 200, has username, characters
  test('GET /api/v1/member returns member info with username and characters', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/member`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.username).toBe(USERNAME);
    expect(body).toHaveProperty('characters');
    expect(Array.isArray(body.characters)).toBe(true);
    expect(body).toHaveProperty('id');
    expect(body.id).toBe(MEMBER_ID);
    // Should also have authProvider
    expect(body).toHaveProperty('authProvider');
  });

  // 2. POST /api/v1/member/character - test with character name (may fail since characters already exist)
  test('POST /api/v1/member/character returns non-500 response', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/member/character`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterName: 'TestCharacterName' },
    });
    // Should not be 500 - may be 400 because characters already exist or API key issue
    expect(res.status()).not.toBe(500);
    expect([200, 400]).toContain(res.status());
  });

  // 3. POST /api/v1/member/password - 400 (validation: wrong current password)
  test('POST /api/v1/member/password returns 400 for wrong current password', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/member/password`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        oldPassword: 'wrong-password-12345',
        newPassword: 'new-password-12345',
      },
    });
    expect(res.status()).toBe(400);
    const body = await res.json();
    // Should have an error message about password mismatch
    expect(body).toHaveProperty('message');
  });

  // 4. PATCH /api/v1/member/main-character - update main character name
  test('PATCH /api/v1/member/main-character updates main character', async ({ request }) => {
    // First, get current state
    const getMember = await request.get(`${BASE}/api/v1/member`, {
      headers: auth(),
    });
    const memberData = await getMember.json();
    const originalMainChar = memberData.mainCharacter;

    // Update to a test value
    const testMainChar = '테스트 계정 4';
    const res = await request.patch(`${BASE}/api/v1/member/main-character`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { mainCharacter: testMainChar },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Verify change took effect
    const verify = await request.get(`${BASE}/api/v1/member`, {
      headers: auth(),
    });
    const verifyData = await verify.json();
    expect(verifyData.mainCharacter).toBe(testMainChar);

    // Restore original value
    if (originalMainChar !== null && originalMainChar !== undefined) {
      await request.patch(`${BASE}/api/v1/member/main-character`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: { mainCharacter: originalMainChar },
      });
    }
  });

  // 5. PATCH /api/v1/member/provider - test provider change (needs password)
  test('PATCH /api/v1/member/provider returns non-500 response', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/member/provider`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { password: 'test-provider-password' },
    });
    // May succeed (200) or fail (400) depending on current provider state, but should not 500
    expect(res.status()).not.toBe(500);
    expect([200, 400]).toContain(res.status());
  });

  // 6. POST /api/v1/member/ads - test ads toggle
  test('POST /api/v1/member/ads records ad view', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/member/ads`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {},
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Verify adsDate was set
    const verify = await request.get(`${BASE}/api/v1/member`, {
      headers: auth(),
    });
    const verifyData = await verify.json();
    expect(verifyData.adsDate).not.toBeNull();
  });

  // 7. PATCH /api/v1/member/api-key - test API key update (may fail with invalid key)
  test('PATCH /api/v1/member/api-key returns 400 for invalid API key', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/member/api-key`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { apiKey: 'invalid-api-key-for-testing' },
    });
    // Should return 400 because the API key is invalid (Lostark API validation fails)
    expect(res.status()).toBe(400);
    const body = await res.json();
    expect(body).toHaveProperty('message');
  });

  test('PATCH /api/v1/member/api-key returns 400 for empty API key', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/member/api-key`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { apiKey: '' },
    });
    expect(res.status()).toBe(400);
  });

  // 8. DELETE /api/v1/member/characters - verify endpoint exists without actually deleting
  test('DELETE /api/v1/member/characters returns 401 without auth', async ({ request }) => {
    const res = await request.delete(`${BASE}/api/v1/member/characters`);
    // Without auth, should get 401
    expect(res.status()).toBe(401);
  });

  test('DELETE /api/v1/member/characters returns 405 for wrong method (GET)', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/member/characters`, {
      headers: auth(),
    });
    // GET on a DELETE-only route should return 405 Method Not Allowed
    expect(res.status()).toBe(405);
  });
});


// ============================================================================
// CHARACTER CRUD
// ============================================================================
test.describe('Character CRUD', () => {

  // 9. PATCH /api/v1/character/settings - update settings for character
  test('PATCH /api/v1/character/settings updates character settings', async ({ request }) => {
    // Toggle showChaos off
    const res = await request.patch(`${BASE}/api/v1/character/settings`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        name: 'showChaos',
        value: false,
      },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Toggle showChaos back on to restore
    const restore = await request.patch(`${BASE}/api/v1/character/settings`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        name: 'showChaos',
        value: true,
      },
    });
    expect(restore.status()).toBe(200);
  });

  test('PATCH /api/v1/character/settings returns 400 for unknown setting name', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/character/settings`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        name: 'unknownSetting',
        value: true,
      },
    });
    expect(res.status()).toBe(400);
  });

  // 10. PATCH /api/v1/character/gold-character - toggle gold character
  test('PATCH /api/v1/character/gold-character toggles gold character', async ({ request }) => {
    // Toggle once
    const res1 = await request.patch(`${BASE}/api/v1/character/gold-character`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Toggle back to restore original state
    const res2 = await request.patch(`${BASE}/api/v1/character/gold-character`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res2.status()).toBe(200);
    const body2 = await res2.json();
    expect(body2.message).toBe('ok');
  });

  // 11. POST /api/v1/character/memo - update memo
  test('POST /api/v1/character/memo updates character memo', async ({ request }) => {
    // Get original memo
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);
    const originalMemo = char?.memo || '';

    // Set test memo
    const testMemo = 'Playwright test memo';
    const res = await request.post(`${BASE}/api/v1/character/memo`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        memo: testMemo,
      },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Verify memo was updated
    const verifyList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const verifyChars = await verifyList.json();
    const verifyChar = verifyChars.find((c: any) => c.characterId === CHARACTER_ID_1);
    expect(verifyChar.memo).toBe(testMemo);

    // Restore original memo
    await request.post(`${BASE}/api/v1/character/memo`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        memo: originalMemo,
      },
    });
  });

  // 12. PATCH /api/v1/character/deleted - toggle deleted (then toggle back)
  test('PATCH /api/v1/character/deleted toggles deleted status and restores', async ({ request }) => {
    // Toggle to deleted
    const res1 = await request.patch(`${BASE}/api/v1/character/deleted`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Verify character is now in deleted list
    const deletedList = await request.get(`${BASE}/api/v1/character-list/deleted`, {
      headers: auth(),
    });
    expect(deletedList.status()).toBe(200);
    const deletedChars = await deletedList.json();
    const isDeleted = deletedChars.some((c: any) => c.characterId === CHARACTER_ID_1);
    expect(isDeleted).toBe(true);

    // Toggle back to restore
    const res2 = await request.patch(`${BASE}/api/v1/character/deleted`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res2.status()).toBe(200);
    const body2 = await res2.json();
    expect(body2.message).toBe('ok');

    // Verify character is back in active list
    const activeList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const activeChars = await activeList.json();
    const isActive = activeChars.some((c: any) => c.characterId === CHARACTER_ID_1);
    expect(isActive).toBe(true);
  });

  // 13. PATCH /api/v1/character/name - test name change
  test('PATCH /api/v1/character/name returns non-500 response', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/character/name`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        characterName: '테스트 계정 4',
      },
    });
    // Should succeed (200) or fail with 400 if name validation fails, but not 500
    expect(res.status()).not.toBe(500);
    expect([200, 400]).toContain(res.status());
  });
});


// ============================================================================
// CHARACTER DAY
// ============================================================================
test.describe('Character Day endpoints', () => {

  // 14. POST /api/v1/character/day/check - check day content for chaos
  test('POST /api/v1/character/day/check checks chaos content', async ({ request }) => {
    // Check chaos
    const res1 = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'chaos',
      },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1).toHaveProperty('chaosCheck');
    expect(body1).toHaveProperty('chaosGauge');
    expect(body1).toHaveProperty('guardianCheck');
    expect(body1).toHaveProperty('guardianGauge');
    expect(body1).toHaveProperty('eponaCheck');
    expect(body1).toHaveProperty('eponaGauge');

    // Toggle back to restore (chaos toggles between 0 and 2)
    const res2 = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'chaos',
      },
    });
    expect(res2.status()).toBe(200);
  });

  test('POST /api/v1/character/day/check checks guardian content', async ({ request }) => {
    const res1 = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'guardian',
      },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1).toHaveProperty('guardianCheck');

    // Toggle back
    const res2 = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'guardian',
      },
    });
    expect(res2.status()).toBe(200);
  });

  test('POST /api/v1/character/day/check checks epona content', async ({ request }) => {
    const res1 = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'epona',
      },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1).toHaveProperty('eponaCheck');

    // Toggle back (epona cycles 0->1->2->3->0, so call 3 more times to get back)
    for (let i = 0; i < 3; i++) {
      await request.post(`${BASE}/api/v1/character/day/check`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          category: 'epona',
        },
      });
    }
  });

  test('POST /api/v1/character/day/check returns 400 for unknown category', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'unknown',
      },
    });
    expect(res.status()).toBe(400);
  });

  // 15. POST /api/v1/character/day/gauge - update gauge
  test('POST /api/v1/character/day/gauge updates rest gauges', async ({ request }) => {
    // Get current gauge values
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);
    const originalChaosGauge = char?.chaosGauge ?? 0;
    const originalGuardianGauge = char?.guardianGauge ?? 0;

    // Set new gauge values
    const res = await request.post(`${BASE}/api/v1/character/day/gauge`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        chaosGauge: 40,
        guardianGauge: 20,
      },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Restore original values
    await request.post(`${BASE}/api/v1/character/day/gauge`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        chaosGauge: originalChaosGauge,
        guardianGauge: originalGuardianGauge,
      },
    });
  });

  // 16. POST /api/v1/character/day/check/all - check all day content for one character
  test('POST /api/v1/character/day/check/all checks all daily content', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/day/check/all`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
      },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body).toHaveProperty('chaosCheck');
    expect(body).toHaveProperty('guardianCheck');
    expect(body).toHaveProperty('eponaCheck');
    // After checking all: chaos should be 2, guardian >= 1, epona should be 3
    expect(body.chaosCheck).toBe(2);
    expect(body.guardianCheck).toBeGreaterThanOrEqual(1);
    expect(body.eponaCheck).toBe(3);
  });

  // 17. POST /api/v1/character/day/check/all-characters - check all characters day content
  test('POST /api/v1/character/day/check/all-characters checks all characters', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/day/check/all-characters`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        serverName: SERVER_NAME,
      },
    });
    // May succeed or fail depending on character is_deleted state, should not be 500
    expect(res.status()).not.toBe(500);
    expect([200, 400]).toContain(res.status());
  });
});


// ============================================================================
// CHARACTER WEEK
// ============================================================================
test.describe('Character Week endpoints', () => {

  // 18. GET /api/v1/character/week/raid/form - get raid form
  test('GET /api/v1/character/week/raid/form returns raid form data', async ({ request }) => {
    const res = await request.get(
      `${BASE}/api/v1/character/week/raid/form?characterId=${CHARACTER_ID_1}`,
      { headers: auth() }
    );
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    // Each entry should have raid form fields
    if (body.length > 0) {
      expect(body[0]).toHaveProperty('id');
      expect(body[0]).toHaveProperty('weekCategory');
      expect(body[0]).toHaveProperty('weekContentName');
      expect(body[0]).toHaveProperty('weekContentGate');
      expect(body[0]).toHaveProperty('gold');
      expect(body[0]).toHaveProperty('itemLevel');
      expect(body[0]).toHaveProperty('selected');
    }
  });

  test('GET /api/v1/character/week/raid/form returns 400 without characterId', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/character/week/raid/form`, {
      headers: auth(),
    });
    expect(res.status()).toBe(400);
  });

  // 19. POST /api/v1/character/week/raid/check - check raid
  test('POST /api/v1/character/week/raid/check toggles raid check', async ({ request }) => {
    // First get the character's todo list to find a valid weekCategory
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);

    if (char && char.todoList && char.todoList.length > 0) {
      const weekCategory = char.todoList[0].weekCategory;

      // Toggle check
      const res1 = await request.post(`${BASE}/api/v1/character/week/raid/check`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: weekCategory,
        },
      });
      expect(res1.status()).toBe(200);
      const body1 = await res1.json();
      expect(body1.message).toBe('ok');

      // Toggle back to restore
      const res2 = await request.post(`${BASE}/api/v1/character/week/raid/check`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: weekCategory,
        },
      });
      expect(res2.status()).toBe(200);
    } else {
      // No todo list, just verify the endpoint accepts the request
      const res = await request.post(`${BASE}/api/v1/character/week/raid/check`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: 'NonExistentRaid',
        },
      });
      expect(res.status()).not.toBe(500);
    }
  });

  // 20. POST /api/v1/character/week/raid/message - update raid message
  test('POST /api/v1/character/week/raid/message updates raid message', async ({ request }) => {
    // Get a valid weekCategory
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);

    if (char && char.todoList && char.todoList.length > 0) {
      const weekCategory = char.todoList[0].weekCategory;
      const originalMessage = char.todoList[0].message || '';

      // Set test message
      const res = await request.post(`${BASE}/api/v1/character/week/raid/message`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: weekCategory,
          message: 'Playwright test message',
        },
      });
      expect(res.status()).toBe(200);
      const body = await res.json();
      expect(body.message).toBe('ok');

      // Restore original message
      await request.post(`${BASE}/api/v1/character/week/raid/message`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: weekCategory,
          message: originalMessage,
        },
      });
    } else {
      // No todo list, just verify the endpoint accepts the request format
      const res = await request.post(`${BASE}/api/v1/character/week/raid/message`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: {
          characterId: CHARACTER_ID_1,
          weekCategory: 'NonExistentRaid',
          message: 'test',
        },
      });
      expect(res.status()).not.toBe(500);
    }
  });

  // 21. POST /api/v1/character/week/epona - check week epona
  test('POST /api/v1/character/week/epona toggles weekly epona', async ({ request }) => {
    // Get current state
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);
    const originalWeekEpona = char?.weekEpona ?? 0;

    // Increment by 1
    const res1 = await request.post(`${BASE}/api/v1/character/week/epona`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        allCheck: false,
      },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Test allCheck toggle (sets to 3 or 0)
    const res2 = await request.post(`${BASE}/api/v1/character/week/epona`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        allCheck: true,
      },
    });
    expect(res2.status()).toBe(200);

    // Restore: toggle allCheck again to reset to 0 if it was set to 3, or set to 3 if it was 0
    // Just call allCheck twice to get back to original-ish state
    await request.post(`${BASE}/api/v1/character/week/epona`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        allCheck: true,
      },
    });
  });

  // 22. POST /api/v1/character/week/silmael - toggle silmael
  test('POST /api/v1/character/week/silmael toggles silmael exchange', async ({ request }) => {
    // Toggle once
    const res1 = await request.post(`${BASE}/api/v1/character/week/silmael`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Toggle back to restore
    const res2 = await request.post(`${BASE}/api/v1/character/week/silmael`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res2.status()).toBe(200);
  });

  // 23. POST /api/v1/character/week/cube - update cube ticket
  test('POST /api/v1/character/week/cube updates cube ticket count', async ({ request }) => {
    // Get current state
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    const characters = await charList.json();
    const char = characters.find((c: any) => c.characterId === CHARACTER_ID_1);
    const originalCubeTicket = char?.cubeTicket ?? 0;

    // Set cube ticket to 5
    const res = await request.post(`${BASE}/api/v1/character/week/cube`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        num: 5,
      },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.message).toBe('ok');

    // Restore original value
    await request.post(`${BASE}/api/v1/character/week/cube`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        num: originalCubeTicket,
      },
    });
  });

  // 24. PATCH /api/v1/character/week/gold-check-version - toggle gold check version
  test('PATCH /api/v1/character/week/gold-check-version toggles gold check version', async ({ request }) => {
    // Toggle once
    const res1 = await request.patch(`${BASE}/api/v1/character/week/gold-check-version`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Toggle back to restore
    const res2 = await request.patch(`${BASE}/api/v1/character/week/gold-check-version`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res2.status()).toBe(200);
  });

  // 25. POST /api/v1/character/week/elysian - update elysian
  test('POST /api/v1/character/week/elysian increments and decrements elysian count', async ({ request }) => {
    // Increment
    const res1 = await request.post(`${BASE}/api/v1/character/week/elysian`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        action: 'increment',
      },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Decrement to restore
    const res2 = await request.post(`${BASE}/api/v1/character/week/elysian`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        action: 'decrement',
      },
    });
    expect(res2.status()).toBe(200);
  });

  test('POST /api/v1/character/week/elysian returns 400 for unknown action', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/week/elysian`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        action: 'unknownAction',
      },
    });
    expect(res.status()).toBe(400);
  });

  // 26. POST /api/v1/character/week/elysian/all - toggle elysian all
  test('POST /api/v1/character/week/elysian/all toggles all elysian', async ({ request }) => {
    // Toggle once
    const res1 = await request.post(`${BASE}/api/v1/character/week/elysian/all`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res1.status()).toBe(200);
    const body1 = await res1.json();
    expect(body1.message).toBe('ok');

    // Toggle back to restore
    const res2 = await request.post(`${BASE}/api/v1/character/week/elysian/all`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res2.status()).toBe(200);
  });
});


// ============================================================================
// CHARACTER LIST
// ============================================================================
test.describe('Character List endpoints', () => {

  // 27. PATCH /api/v1/character-list/sorting - update sorting
  test('PATCH /api/v1/character-list/sorting updates character sort order', async ({ request }) => {
    // Get current character list to know sort order
    const charList = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    expect(charList.status()).toBe(200);
    const characters = await charList.json();
    expect(Array.isArray(characters)).toBe(true);

    if (characters.length >= 2) {
      // Save original sort
      const originalSort = characters.map((c: any) => ({
        characterId: c.characterId,
        sortNumber: c.sortNumber,
      }));

      // Swap first two characters' sort numbers
      const newSort = [
        { characterId: characters[0].characterId, sortNumber: characters[1].sortNumber },
        { characterId: characters[1].characterId, sortNumber: characters[0].sortNumber },
      ];

      const res = await request.patch(`${BASE}/api/v1/character-list/sorting`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: newSort,
      });
      expect(res.status()).toBe(200);
      const body = await res.json();
      expect(body.message).toBe('ok');

      // Restore original sort order
      await request.patch(`${BASE}/api/v1/character-list/sorting`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: originalSort,
      });
    } else if (characters.length === 1) {
      // Only one character, just test with the same sort number
      const res = await request.patch(`${BASE}/api/v1/character-list/sorting`, {
        headers: { ...auth(), 'Content-Type': 'application/json' },
        data: [
          { characterId: characters[0].characterId, sortNumber: characters[0].sortNumber },
        ],
      });
      expect(res.status()).toBe(200);
    }
  });

  test('PATCH /api/v1/character-list/sorting returns 400 for invalid body', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/character-list/sorting`, {
      headers: { ...auth(), 'Content-Type': 'application/json' },
      data: 'not-an-array',
    });
    expect(res.status()).toBe(400);
  });

  // Additional: GET /api/v1/character-list - verify full character list response
  test('GET /api/v1/character-list returns full character list with todo info', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/character-list`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);

    if (body.length > 0) {
      const char = body[0];
      // Verify response structure matches CharacterResponse
      expect(char).toHaveProperty('characterId');
      expect(char).toHaveProperty('characterName');
      expect(char).toHaveProperty('serverName');
      expect(char).toHaveProperty('itemLevel');
      expect(char).toHaveProperty('sortNumber');
      expect(char).toHaveProperty('goldCharacter');
      expect(char).toHaveProperty('settings');
      expect(char).toHaveProperty('chaosCheck');
      expect(char).toHaveProperty('guardianCheck');
      expect(char).toHaveProperty('eponaCheck');
      expect(char).toHaveProperty('weekEpona');
      expect(char).toHaveProperty('silmaelChange');
      expect(char).toHaveProperty('cubeTicket');
      expect(char).toHaveProperty('todoList');
      expect(Array.isArray(char.todoList)).toBe(true);

      // Verify settings structure
      expect(char.settings).toHaveProperty('showCharacter');
      expect(char.settings).toHaveProperty('showChaos');
      expect(char.settings).toHaveProperty('showGuardian');
      expect(char.settings).toHaveProperty('showEpona');
    }
  });

  // Additional: GET /api/v1/character-list/deleted
  test('GET /api/v1/character-list/deleted returns deleted characters array', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/character-list/deleted`, {
      headers: auth(),
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
  });
});


// ============================================================================
// AUTH REQUIRED - Verify endpoints reject unauthenticated requests
// ============================================================================
test.describe('Auth protection', () => {

  test('GET /api/v1/member returns 401 without auth', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/member`);
    expect(res.status()).toBe(401);
  });

  test('PATCH /api/v1/character/settings returns 401 without auth', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/character/settings`, {
      headers: { 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1, name: 'showChaos', value: false },
    });
    expect(res.status()).toBe(401);
  });

  test('POST /api/v1/character/day/check returns 401 without auth', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1, category: 'chaos' },
    });
    expect(res.status()).toBe(401);
  });

  test('GET /api/v1/character/week/raid/form returns 401 without auth', async ({ request }) => {
    const res = await request.get(
      `${BASE}/api/v1/character/week/raid/form?characterId=${CHARACTER_ID_1}`
    );
    expect(res.status()).toBe(401);
  });

  test('GET /api/v1/character-list returns 401 without auth', async ({ request }) => {
    const res = await request.get(`${BASE}/api/v1/character-list`);
    expect(res.status()).toBe(401);
  });
});


// ============================================================================
// OWNERSHIP PROTECTION - Verify cannot access other users' characters
// ============================================================================
test.describe('Ownership protection', () => {

  const OTHER_TOKEN = createJWT('nonexistent@test.com');
  function otherAuth() { return { Authorization: `Bearer ${OTHER_TOKEN}` }; }

  test('PATCH /api/v1/character/settings returns 400 for non-owned character', async ({ request }) => {
    const res = await request.patch(`${BASE}/api/v1/character/settings`, {
      headers: { ...otherAuth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        name: 'showChaos',
        value: false,
      },
    });
    // Should fail because the user doesn't own this character (400 "member not found" or "permission denied")
    expect(res.status()).toBe(400);
  });

  test('POST /api/v1/character/day/check returns 400 for non-owned character', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/day/check`, {
      headers: { ...otherAuth(), 'Content-Type': 'application/json' },
      data: {
        characterId: CHARACTER_ID_1,
        category: 'chaos',
      },
    });
    expect(res.status()).toBe(400);
  });

  test('POST /api/v1/character/week/silmael returns 400 for non-owned character', async ({ request }) => {
    const res = await request.post(`${BASE}/api/v1/character/week/silmael`, {
      headers: { ...otherAuth(), 'Content-Type': 'application/json' },
      data: { characterId: CHARACTER_ID_1 },
    });
    expect(res.status()).toBe(400);
  });
});
