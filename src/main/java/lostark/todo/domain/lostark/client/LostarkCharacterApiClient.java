package lostark.todo.domain.lostark.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.ArkgridEffectDto;
import lostark.todo.domain.inspection.dto.CardApiResponse;
import lostark.todo.domain.inspection.dto.CardDto;
import lostark.todo.domain.inspection.dto.CardSetEffectDto;
import lostark.todo.domain.inspection.dto.EngravingDto;
import lostark.todo.domain.inspection.dto.EquipmentDto;
import lostark.todo.domain.inspection.dto.GemDto;
import lostark.todo.domain.inspection.dto.ArkPassiveApiResponse;
import lostark.todo.domain.inspection.dto.ArkPassiveDto;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.entity.WeekTodo;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LostarkCharacterApiClient {

    private final LostarkApiClient apiClient;
    private final ContentRepository contentRepository;

    /**
     * 대표캐릭터와 연동된 캐릭터 호출(api 검증)
     *
     * @param characterName
     * @param apiKey
     * @return
     */
    @Transactional
    public List<Character> createCharacterList(String characterName, String apiKey) {
        try {
            JSONArray jsonArray = findCharacters(characterName, apiKey);

            // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
            Map<Category, List<DayContent>> dayContent = contentRepository.getDayContents();

            List<Character> characterList = new ArrayList<>();
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;

                Character character = Character.builder()
                        .characterName(jsonObject.get("CharacterName").toString())
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .characterClassName(jsonObject.get("CharacterClassName").toString())
                        .serverName(jsonObject.get("ServerName").toString())
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemAvgLevel").toString().replace(",", "")))
                        .dayTodo(new DayTodo())
                        .weekTodo(new WeekTodo())
                        .build();
                character.setSettings(new Settings());
                character.setTodoV2List(new ArrayList<>());
                getCharacterImageAndCombatPower(character, apiKey);
                character.getDayTodo().createDayContent(
                        dayContent.get(Category.카오스던전), dayContent.get(Category.가디언토벌), character.getItemLevel());
                characterList.add(character);
            }
            //레벨순으로 정렬 후 리턴
            AtomicInteger sortNumber = new AtomicInteger();
            List<Character> sortedList = characterList.stream()
                    .sorted(Comparator.comparing(Character::getItemLevel).reversed()).collect(Collectors.toList())
                    .stream().map(character -> {
                        character.setSortNumber(sortNumber.getAndIncrement());
                        return character;
                    })
                    .collect(Collectors.toList());
            return sortedList;
        } catch (NullPointerException e) {
            throw new ConditionNotMetException("존재하지 않는 캐릭터명 입니다.");
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 캐릭터 리스트 출력
     */
    public JSONArray findCharacters(String characterName, String apiKey) {
        String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/" + encodeCharacterName + "/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try {
            JSONArray parse = (JSONArray) parser.parse(inputStreamReader);
            return filterLevel(parse);
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // 1415이상만 필터링 메소드
    private JSONArray filterLevel(JSONArray jsonArray) {
        JSONArray filteredArray = new JSONArray();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            double itemMaxLevel = Double.parseDouble(jsonObject.get("ItemAvgLevel").toString().replace(",", ""));
            if (itemMaxLevel >= 1415D) {
                filteredArray.add(jsonObject);
            }
        }
        if (filteredArray.isEmpty()) {
            throw new ConditionNotMetException("아이템 레벨 1415 이상 캐릭터가 없습니다.");
        }

        return filteredArray;
    }

    public String getCharacterImageUrl(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";
            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject profile = (JSONObject) parser.parse(inputStreamReader);
            if (profile != null && profile.get("CharacterImage") != null) {
                return profile.get("CharacterImage").toString();
            }
            return null;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getCharacterImageAndCombatPower(Character character, String apiKey) {
        try {
            String characterName = character.getCharacterName();
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";
            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject profile = (JSONObject) parser.parse(inputStreamReader);
            if (profile != null && profile.get("CharacterImage") != null) {
                character.setCharacterImage(profile.get("CharacterImage").toString());
            }
            if (profile != null && profile.get("CombatPower") != null) {
                String combatPowerStr = profile.get("CombatPower").toString().replace(",", "");
                double newCombatPower = Double.parseDouble(combatPowerStr);
                if (newCombatPower > character.getCombatPower()) {
                    character.setCombatPower(newCombatPower);
                }
            }
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 군장검사용 프로필 조회 (레벨 제한 없음)
     */
    public CharacterJsonDto getCharacterProfileForInspection(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/profiles";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            ObjectMapper objectMapper = new ObjectMapper();
            CharacterJsonDto character = objectMapper.readValue(reader, CharacterJsonDto.class);

            if (character == null) {
                throw new ConditionNotMetException("캐릭터를 찾을 수 없습니다.");
            }

            return character;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("프로필 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 아크그리드 Effects 조회
     */
    public List<ArkgridEffectDto> getArkgridEffects(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/arkgrid";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject arkgrid = (JSONObject) parser.parse(reader);

            List<ArkgridEffectDto> effects = new ArrayList<>();
            if (arkgrid != null && arkgrid.get("Effects") != null) {
                JSONArray effectsArray = (JSONArray) arkgrid.get("Effects");
                for (Object obj : effectsArray) {
                    JSONObject effect = (JSONObject) obj;
                    effects.add(new ArkgridEffectDto(
                            effect.get("Name").toString(),
                            Integer.parseInt(effect.get("Level").toString()),
                            effect.get("Tooltip") != null ? effect.get("Tooltip").toString() : null
                    ));
                }
            }
            return effects;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("아크그리드 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 장비 정보 조회
     */
    public List<EquipmentDto> getEquipment(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/equipment";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONArray equipmentArray = (JSONArray) parser.parse(reader);

            List<EquipmentDto> equipments = new ArrayList<>();
            if (equipmentArray != null) {
                for (Object obj : equipmentArray) {
                    JSONObject item = (JSONObject) obj;
                    equipments.add(new EquipmentDto(
                            item.get("Type") != null ? item.get("Type").toString() : null,
                            item.get("Name") != null ? item.get("Name").toString() : null,
                            item.get("Icon") != null ? item.get("Icon").toString() : null,
                            item.get("Grade") != null ? item.get("Grade").toString() : null,
                            item.get("Tooltip") != null ? item.get("Tooltip").toString() : null
                    ));
                }
            }
            return equipments;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("장비 정보 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 아크패시브 정보 조회 (Points + Effects)
     */
    public ArkPassiveApiResponse getArkPassive(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/arkpassive";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject arkPassiveObj = (JSONObject) parser.parse(reader);

            List<ArkPassiveDto> effects = new ArrayList<>();
            String pointsJson = null;

            if (arkPassiveObj != null) {
                // Points 배열을 JSON 문자열로 저장
                if (arkPassiveObj.get("Points") != null) {
                    JSONArray pointsArray = (JSONArray) arkPassiveObj.get("Points");
                    org.json.simple.JSONArray pointsForSave = new org.json.simple.JSONArray();
                    for (Object obj : pointsArray) {
                        JSONObject point = (JSONObject) obj;
                        org.json.simple.JSONObject p = new org.json.simple.JSONObject();
                        p.put("name", point.get("Name") != null ? point.get("Name").toString() : null);
                        p.put("value", point.get("Value") != null ? Integer.parseInt(point.get("Value").toString()) : 0);
                        p.put("tooltip", point.get("Tooltip") != null ? point.get("Tooltip").toString() : null);
                        pointsForSave.add(p);
                    }
                    pointsJson = pointsForSave.toJSONString();
                }

                // Effects 배열에서 각 계열별 스킬 파싱
                if (arkPassiveObj.get("Effects") != null) {
                    JSONArray effectsArray = (JSONArray) arkPassiveObj.get("Effects");
                    for (Object obj : effectsArray) {
                        JSONObject effectGroup = (JSONObject) obj;
                        String category = effectGroup.get("Name") != null ? effectGroup.get("Name").toString() : null;

                        if (effectGroup.get("Skills") != null) {
                            JSONArray skillsArray = (JSONArray) effectGroup.get("Skills");
                            for (Object skillObj : skillsArray) {
                                JSONObject skill = (JSONObject) skillObj;
                                effects.add(new ArkPassiveDto(
                                        category,
                                        skill.get("Name") != null ? skill.get("Name").toString() : null,
                                        skill.get("Level") != null ? Integer.parseInt(skill.get("Level").toString()) : 0,
                                        skill.get("Icon") != null ? skill.get("Icon").toString() : null,
                                        skill.get("Description") != null ? skill.get("Description").toString() : null
                                ));
                            }
                        }
                    }
                }
            }

            return new ArkPassiveApiResponse(pointsJson, effects);
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("아크패시브 정보 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new ArkPassiveApiResponse(null, new ArrayList<>());
        }
    }

    /**
     * 각인 정보 조회 (ArkPassiveEffects)
     */
    public List<EngravingDto> getEngravings(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/engravings";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject engravingsObj = (JSONObject) parser.parse(reader);

            List<EngravingDto> engravings = new ArrayList<>();
            if (engravingsObj != null && engravingsObj.get("ArkPassiveEffects") != null) {
                JSONArray effectsArray = (JSONArray) engravingsObj.get("ArkPassiveEffects");
                for (Object obj : effectsArray) {
                    JSONObject effect = (JSONObject) obj;
                    engravings.add(new EngravingDto(
                            effect.get("Name") != null ? effect.get("Name").toString() : null,
                            effect.get("Level") != null ? Integer.parseInt(effect.get("Level").toString()) : 0,
                            effect.get("Grade") != null ? effect.get("Grade").toString() : null,
                            effect.get("AbilityStoneLevel") != null ? Integer.parseInt(effect.get("AbilityStoneLevel").toString()) : null
                    ));
                }
            }
            return engravings;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("각인 정보 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 보석 정보 조회 (Gems + Effects.Skills)
     */
    public List<GemDto> getGems(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/gems";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject gemsObj = (JSONObject) parser.parse(reader);

            List<GemDto> gems = new ArrayList<>();
            if (gemsObj == null) {
                return gems;
            }

            // Gems 배열에서 슬롯별 레벨 매핑
            Map<Long, Integer> gemLevelBySlot = new java.util.HashMap<>();
            if (gemsObj.get("Gems") != null) {
                JSONArray gemsArray = (JSONArray) gemsObj.get("Gems");
                for (Object obj : gemsArray) {
                    JSONObject gem = (JSONObject) obj;
                    long slot = (long) gem.get("Slot");
                    int level = gem.get("Level") != null ? Integer.parseInt(gem.get("Level").toString()) : 0;
                    gemLevelBySlot.put(slot, level);
                }
            }

            // Effects.Skills 배열에서 스킬 상세 정보 파싱
            if (gemsObj.get("Effects") != null) {
                JSONObject effectsObj = (JSONObject) gemsObj.get("Effects");
                if (effectsObj.get("Skills") != null) {
                    JSONArray skillsArray = (JSONArray) effectsObj.get("Skills");
                    for (Object obj : skillsArray) {
                        JSONObject skill = (JSONObject) obj;
                        int gemSlot = skill.get("GemSlot") != null ? Integer.parseInt(skill.get("GemSlot").toString()) : -1;
                        int gemLevel = gemLevelBySlot.getOrDefault((long) gemSlot, 0);

                        gems.add(new GemDto(
                                skill.get("Name") != null ? skill.get("Name").toString() : null,
                                gemLevel,
                                skill.get("Description") != null ? skill.get("Description").toString() : null,
                                skill.get("Option") != null ? skill.get("Option").toString() : null,
                                skill.get("Icon") != null ? skill.get("Icon").toString() : null
                        ));
                    }
                }
            }

            return gems;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("보석 정보 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 카드 정보 조회 (Cards + Effects)
     */
    public CardApiResponse getCards(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/cards";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject cardsObj = (JSONObject) parser.parse(reader);

            List<CardDto> cards = new ArrayList<>();
            List<CardSetEffectDto> cardSetEffects = new ArrayList<>();

            if (cardsObj != null) {
                // Cards 배열 파싱
                if (cardsObj.get("Cards") != null) {
                    JSONArray cardsArray = (JSONArray) cardsObj.get("Cards");
                    for (Object obj : cardsArray) {
                        JSONObject card = (JSONObject) obj;
                        cards.add(new CardDto(
                                card.get("Name") != null ? card.get("Name").toString() : null,
                                card.get("Icon") != null ? card.get("Icon").toString() : null,
                                card.get("AwakeCount") != null ? Integer.parseInt(card.get("AwakeCount").toString()) : 0,
                                card.get("AwakeTotal") != null ? Integer.parseInt(card.get("AwakeTotal").toString()) : 0,
                                card.get("Grade") != null ? card.get("Grade").toString() : null
                        ));
                    }
                }

                // Effects 배열 파싱 (세트 효과)
                if (cardsObj.get("Effects") != null) {
                    JSONArray effectsArray = (JSONArray) cardsObj.get("Effects");
                    for (Object obj : effectsArray) {
                        JSONObject effectGroup = (JSONObject) obj;
                        if (effectGroup.get("Items") != null) {
                            JSONArray items = (JSONArray) effectGroup.get("Items");
                            for (Object itemObj : items) {
                                JSONObject item = (JSONObject) itemObj;
                                cardSetEffects.add(new CardSetEffectDto(
                                        item.get("Name") != null ? item.get("Name").toString() : null,
                                        item.get("Description") != null ? item.get("Description").toString() : null
                                ));
                            }
                        }
                    }
                }
            }

            return new CardApiResponse(cards, cardSetEffects);
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (Exception e) {
            log.warn("카드 정보 조회 실패 - 캐릭터: {}, 오류: {}", characterName, e.getMessage());
            return new CardApiResponse(new ArrayList<>(), new ArrayList<>());
        }
    }

    public CharacterJsonDto getCharacter(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/profiles";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            ObjectMapper objectMapper = new ObjectMapper();
            CharacterJsonDto character = objectMapper.readValue(reader, CharacterJsonDto.class);

            if (character == null) {
                throw new ConditionNotMetException("캐릭터를 찾을 수 없습니다. (인게임에서 한번 접속해주세요.)");
            }

            if (character.getItemAvgLevel() < 1415.00) {
                throw new ConditionNotMetException("로아투두는 아이템 레벨 1415 이상 캐릭터만 저장할 수 있습니다.");
            }

            return character;
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("API 응답 파싱 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}
