-- 지평의 성당 (어비스 던전) - 2026.03.18 업데이트
-- 귀속 골드 100%, more_reward_gold는 32% 추정값
-- 은총의 파편/코어는 기존 스키마에 컬럼 없어 주석으로 표기

-- 기존 데이터 삭제 (이미 넣은 경우)
DELETE FROM Loatodo.content WHERE week_category = '지평의 성당';

-- 1단계 (입장레벨 1700) - 은총의 파편: 4,6 / 코어: 2,2 / 영웅~고대
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1700, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 1, 13500, '지평의 성당', '_1단계', 4320, 0, 0, 13500);
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1700, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 2, 16500, '지평의 성당', '_1단계', 5280, 0, 0, 16500);

-- 2단계 (입장레벨 1720) - 은총의 파편: 12,18 / 코어: 2,2 / 전설~고대
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1720, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 1, 16000, '지평의 성당', '_2단계', 5120, 0, 0, 16000);
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1720, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 2, 24000, '지평의 성당', '_2단계', 7680, 0, 0, 24000);

-- 3단계 (입장레벨 1750) - 은총의 파편: 24,36 / 코어: 3,3 / 전설~고대
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1750, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 1, 20000, '지평의 성당', '_3단계', 6400, 0, 0, 20000);
INSERT INTO Loatodo.content (dtype, category, level, name, card_exp, jewelry, leap_stone, shilling, solar_blessing, solar_grace, solar_protection, destruction_stone, guardian_stone, honor_shard, cool_time, gate, gold, week_category, week_content_category, more_reward_gold, glaciers_breath, lavas_breath, character_gold)
VALUES ('WeekContent', '어비스던전', 1750, '지평의 성당', null, null, 0, null, null, null, null, 0, 0, 0, 1, 2, 30000, '지평의 성당', '_3단계', 9600, 0, 0, 30000);
