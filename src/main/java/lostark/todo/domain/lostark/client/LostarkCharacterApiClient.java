package lostark.todo.domain.lostark.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterJsonDto;
// import lostark.todo.domain.inspection.dto.ArkgridEffectDto;
// import lostark.todo.domain.inspection.dto.CardApiResponse;
// import lostark.todo.domain.inspection.dto.CardDto;
// import lostark.todo.domain.inspection.dto.CardSetEffectDto;
// import lostark.todo.domain.inspection.dto.EngravingDto;
// import lostark.todo.domain.inspection.dto.EquipmentDto;
// import lostark.todo.domain.inspection.dto.GemDto;
// import lostark.todo.domain.inspection.dto.ArkPassiveApiResponse;
// import lostark.todo.domain.inspection.dto.ArkPassiveEffectDto;
// import lostark.todo.domain.inspection.dto.ArkPassivePointDto;
import lostark.todo.domain.content.enums.Category;
import lostark.todo.domain.content.repository.ContentRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.entity.WeekTodo;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LostarkCharacterApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
            JsonNode jsonArray = findCharacters(characterName, apiKey);

            // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
            Map<Category, List<DayContent>> dayContent = contentRepository.getDayContents();

            List<Character> characterList = new ArrayList<>();
            for (JsonNode jsonObject : jsonArray) {
                Character character = Character.builder()
                        .characterName(jsonObject.get("CharacterName").asText())
                        .characterLevel(jsonObject.get("CharacterLevel").asInt())
                        .characterClassName(jsonObject.get("CharacterClassName").asText())
                        .serverName(jsonObject.get("ServerName").asText())
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemAvgLevel").asText().replace(",", "")))
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
    public JsonNode findCharacters(String characterName, String apiKey) {
        String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/" + encodeCharacterName + "/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        try {
            JsonNode jsonArray = MAPPER.readTree(inputStreamReader);
            return filterLevel(jsonArray);
        } catch (ConditionNotMetException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 1415이상만 필터링 메소드
    private JsonNode filterLevel(JsonNode jsonArray) {
        List<JsonNode> filtered = new ArrayList<>();
        for (JsonNode jsonObject : jsonArray) {
            double itemMaxLevel = Double.parseDouble(jsonObject.get("ItemAvgLevel").asText().replace(",", ""));
            if (itemMaxLevel >= 1415D) {
                filtered.add(jsonObject);
            }
        }
        if (filtered.isEmpty()) {
            throw new ConditionNotMetException("아이템 레벨 1415 이상 캐릭터가 없습니다.");
        }

        return MAPPER.valueToTree(filtered);
    }

    public String getCharacterImageUrl(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";
            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
            JsonNode profile = MAPPER.readTree(inputStreamReader);
            if (profile != null && profile.has("CharacterImage") && !profile.get("CharacterImage").isNull()) {
                return profile.get("CharacterImage").asText();
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
            JsonNode profile = MAPPER.readTree(inputStreamReader);
            if (profile != null && profile.has("CharacterImage") && !profile.get("CharacterImage").isNull()) {
                character.setCharacterImage(profile.get("CharacterImage").asText());
            }
            if (profile != null && profile.has("CombatPower") && !profile.get("CombatPower").isNull()) {
                String combatPowerStr = profile.get("CombatPower").asText().replace(",", "");
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
            CharacterJsonDto character = MAPPER.readValue(reader, CharacterJsonDto.class);

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

    // /**
    //  * 아크그리드 Effects 조회
    //  */
    // public List<ArkgridEffectDto> getArkgridEffects(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/arkgrid";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode arkgrid = MAPPER.readTree(reader);
    //
    //         List<ArkgridEffectDto> effects = new ArrayList<>();
    //         if (arkgrid != null && arkgrid.has("Effects") && arkgrid.get("Effects").isArray()) {
    //             for (JsonNode effect : arkgrid.get("Effects")) {
    //                 effects.add(new ArkgridEffectDto(
    //                         effect.get("Name").asText(),
    //                         effect.get("Level").asInt(),
    //                         effect.has("Tooltip") && !effect.get("Tooltip").isNull()
    //                                 ? effect.get("Tooltip").asText() : null
    //                 ));
    //             }
    //         }
    //         return effects;
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("아크그리드 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new ArrayList<>();
    //     }
    // }

    // /**
    //  * 장비 정보 조회
    //  */
    // public List<EquipmentDto> getEquipment(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/equipment";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode equipmentArray = MAPPER.readTree(reader);
    //
    //         List<EquipmentDto> equipments = new ArrayList<>();
    //         if (equipmentArray != null && equipmentArray.isArray()) {
    //             for (JsonNode item : equipmentArray) {
    //                 equipments.add(new EquipmentDto(
    //                         getTextOrNull(item, "Type"),
    //                         getTextOrNull(item, "Name"),
    //                         getTextOrNull(item, "Icon"),
    //                         getTextOrNull(item, "Grade"),
    //                         getTextOrNull(item, "Tooltip")
    //                 ));
    //             }
    //         }
    //         return equipments;
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("장비 정보 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new ArrayList<>();
    //     }
    // }

    // /**
    //  * 아크패시브 정보 조회 (Title + Points + Effects)
    //  */
    // public ArkPassiveApiResponse getArkPassive(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/arkpassive";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode arkPassiveObj = MAPPER.readTree(reader);
    //
    //         String title = null;
    //         List<ArkPassivePointDto> points = new ArrayList<>();
    //         List<ArkPassiveEffectDto> effects = new ArrayList<>();
    //
    //         if (arkPassiveObj != null && !arkPassiveObj.isNull()) {
    //             title = getTextOrNull(arkPassiveObj, "Title");
    //
    //             // Points 배열 파싱
    //             JsonNode pointsNode = arkPassiveObj.get("Points");
    //             if (pointsNode != null && pointsNode.isArray()) {
    //                 for (JsonNode point : pointsNode) {
    //                     points.add(new ArkPassivePointDto(
    //                             getTextOrNull(point, "Name"),
    //                             point.has("Value") ? point.get("Value").asInt(0) : 0,
    //                             getTextOrNull(point, "Tooltip")
    //                     ));
    //                 }
    //             }
    //
    //             // Effects 배열에서 각 계열별 스킬 파싱
    //             JsonNode effectsNode = arkPassiveObj.get("Effects");
    //             if (effectsNode != null && effectsNode.isArray()) {
    //                 for (JsonNode effectGroup : effectsNode) {
    //                     String category = getTextOrNull(effectGroup, "Name");
    //
    //                     JsonNode skillsNode = effectGroup.get("Skills");
    //                     if (skillsNode != null && skillsNode.isArray()) {
    //                         for (JsonNode skill : skillsNode) {
    //                             String rawName = getTextOrNull(skill, "Name");
    //                             String effectName = stripHtmlTags(rawName);
    //                             String description = getTextOrNull(skill, "Description");
    //                             int tier = parseTierFromDescription(description);
    //
    //                             effects.add(new ArkPassiveEffectDto(
    //                                     category,
    //                                     effectName,
    //                                     getTextOrNull(skill, "Icon"),
    //                                     tier,
    //                                     skill.has("Level") ? skill.get("Level").asInt(0) : 0
    //                             ));
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //
    //         return new ArkPassiveApiResponse(title, points, effects);
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("아크패시브 정보 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new ArkPassiveApiResponse(null, new ArrayList<>(), new ArrayList<>());
    //     }
    // }

    /**
     * HTML 태그 제거 (예: "<FONT color='#83E9FF'>점화 Lv.3</FONT>" -> "점화 Lv.3")
     */
    private String stripHtmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]+>", "").trim();
    }

    /**
     * Description 텍스트에서 tier 값 파싱 (예: "[진화] ..." -> tier 추출)
     * 깨달음=1, 도약=2, 진화=3 매핑. 매칭 실패시 0.
     */
    private int parseTierFromDescription(String description) {
        if (description == null) return 0;
        if (description.contains("[진화]")) return 3;
        if (description.contains("[도약]")) return 2;
        if (description.contains("[깨달음]")) return 1;
        return 0;
    }

    // /**
    //  * 각인 정보 조회 (ArkPassiveEffects)
    //  */
    // public List<EngravingDto> getEngravings(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/engravings";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode engravingsObj = MAPPER.readTree(reader);
    //
    //         List<EngravingDto> engravings = new ArrayList<>();
    //         JsonNode effectsNode = engravingsObj != null ? engravingsObj.get("ArkPassiveEffects") : null;
    //         if (effectsNode != null && effectsNode.isArray()) {
    //             for (JsonNode effect : effectsNode) {
    //                 engravings.add(new EngravingDto(
    //                         getTextOrNull(effect, "Name"),
    //                         effect.has("Level") ? effect.get("Level").asInt(0) : 0,
    //                         getTextOrNull(effect, "Grade"),
    //                         effect.has("AbilityStoneLevel") && !effect.get("AbilityStoneLevel").isNull()
    //                                 ? effect.get("AbilityStoneLevel").asInt() : null,
    //                         getTextOrNull(effect, "Description")
    //                 ));
    //             }
    //         }
    //         return engravings;
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("각인 정보 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new ArrayList<>();
    //     }
    // }

    // /**
    //  * 보석 정보 조회 (Gems + Effects.Skills)
    //  */
    // public List<GemDto> getGems(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/gems";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode gemsObj = MAPPER.readTree(reader);
    //
    //         List<GemDto> gems = new ArrayList<>();
    //         if (gemsObj == null || gemsObj.isNull()) {
    //             return gems;
    //         }
    //
    //         // Gems 배열에서 슬롯별 레벨/등급/아이콘 매핑
    //         Map<Long, Integer> gemLevelBySlot = new HashMap<>();
    //         Map<Long, String> gemGradeBySlot = new HashMap<>();
    //         Map<Long, String> gemIconBySlot = new HashMap<>();
    //         JsonNode gemsArrayNode = gemsObj.get("Gems");
    //         if (gemsArrayNode != null && gemsArrayNode.isArray()) {
    //             for (JsonNode gem : gemsArrayNode) {
    //                 long slot = gem.get("Slot").asLong();
    //                 int level = gem.has("Level") ? gem.get("Level").asInt(0) : 0;
    //                 String grade = getTextOrNull(gem, "Grade");
    //                 String icon = getTextOrNull(gem, "Icon");
    //                 gemLevelBySlot.put(slot, level);
    //                 gemGradeBySlot.put(slot, grade);
    //                 gemIconBySlot.put(slot, icon);
    //             }
    //         }
    //
    //         // Effects.Skills 배열에서 스킬 상세 정보 파싱
    //         JsonNode effectsNode = gemsObj.get("Effects");
    //         if (effectsNode != null && !effectsNode.isNull()) {
    //             JsonNode skillsNode = effectsNode.get("Skills");
    //             if (skillsNode != null && skillsNode.isArray()) {
    //                 for (JsonNode skill : skillsNode) {
    //                     int gemSlot = skill.has("GemSlot") ? skill.get("GemSlot").asInt(-1) : -1;
    //                     int gemLevel = gemLevelBySlot.getOrDefault((long) gemSlot, 0);
    //                     String gemGrade = gemGradeBySlot.getOrDefault((long) gemSlot, null);
    //                     String gemIcon = gemIconBySlot.getOrDefault((long) gemSlot, null);
    //
    //                     // Description은 배열로 반환됨 (예: ["몰아치기 피해 9.5% 증가"])
    //                     String description = null;
    //                     JsonNode descNode = skill.get("Description");
    //                     if (descNode != null && !descNode.isNull()) {
    //                         if (descNode.isArray() && descNode.size() > 0) {
    //                             description = descNode.get(0).asText();
    //                         } else if (descNode.isTextual()) {
    //                             description = descNode.asText();
    //                         }
    //                     }
    //
    //                     gems.add(new GemDto(
    //                             getTextOrNull(skill, "Name"),
    //                             gemSlot,
    //                             getTextOrNull(skill, "Icon"),
    //                             gemIcon,
    //                             gemLevel,
    //                             gemGrade,
    //                             description,
    //                             getTextOrNull(skill, "Option")
    //                     ));
    //                 }
    //             }
    //         }
    //
    //         return gems;
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("보석 정보 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new ArrayList<>();
    //     }
    // }

    // /**
    //  * 카드 정보 조회 (Cards + Effects)
    //  */
    // public CardApiResponse getCards(String characterName, String apiKey) {
    //     try {
    //         String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    //         String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/cards";
    //
    //         InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
    //         JsonNode cardsObj = MAPPER.readTree(reader);
    //
    //         List<CardDto> cards = new ArrayList<>();
    //         List<CardSetEffectDto> cardSetEffects = new ArrayList<>();
    //
    //         if (cardsObj != null && !cardsObj.isNull()) {
    //             // Cards 배열 파싱
    //             JsonNode cardsArrayNode = cardsObj.get("Cards");
    //             if (cardsArrayNode != null && cardsArrayNode.isArray()) {
    //                 for (JsonNode card : cardsArrayNode) {
    //                     cards.add(new CardDto(
    //                             card.has("Slot") ? card.get("Slot").asInt(0) : 0,
    //                             getTextOrNull(card, "Name"),
    //                             getTextOrNull(card, "Icon"),
    //                             card.has("AwakeCount") ? card.get("AwakeCount").asInt(0) : 0,
    //                             card.has("AwakeTotal") ? card.get("AwakeTotal").asInt(0) : 0,
    //                             getTextOrNull(card, "Grade")
    //                     ));
    //                 }
    //             }
    //
    //             // Effects 배열 파싱 (세트 효과)
    //             JsonNode effectsNode = cardsObj.get("Effects");
    //             if (effectsNode != null && effectsNode.isArray()) {
    //                 for (JsonNode effectGroup : effectsNode) {
    //                     JsonNode itemsNode = effectGroup.get("Items");
    //                     if (itemsNode != null && itemsNode.isArray()) {
    //                         for (JsonNode item : itemsNode) {
    //                             cardSetEffects.add(new CardSetEffectDto(
    //                                     getTextOrNull(item, "Name"),
    //                                     getTextOrNull(item, "Description")
    //                             ));
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //
    //         return new CardApiResponse(cards, cardSetEffects);
    //     } catch (ConditionNotMetException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.warn("카드 정보 조회 실패 - 캐릭터: {}", characterName, e);
    //         return new CardApiResponse(new ArrayList<>(), new ArrayList<>());
    //     }
    // }

    public CharacterJsonDto getCharacter(String characterName, String apiKey) {
        try {
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String url = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedName + "/profiles";

            InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey);
            CharacterJsonDto character = MAPPER.readValue(reader, CharacterJsonDto.class);

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

    private static String getTextOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asText();
    }

}
