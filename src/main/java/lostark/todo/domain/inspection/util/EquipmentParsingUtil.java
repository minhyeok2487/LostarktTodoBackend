package lostark.todo.domain.inspection.util;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.inspection.dto.EquipmentDto;
import lostark.todo.domain.inspection.entity.EquipmentHistory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EquipmentParsingUtil {

    private static final Pattern ITEM_LEVEL_PATTERN = Pattern.compile("아이템 레벨 (\\d[\\d,]*)");
    private static final Pattern REFINEMENT_PATTERN = Pattern.compile("\\+(\\d+)");
    private static final Pattern ADVANCED_REFINEMENT_PATTERN = Pattern.compile("\\[상급 재련\\]\\s*(\\d+)단계");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern ENGRAVING_PATTERN = Pattern.compile("([가-힣a-zA-Z\\s]+)\\s*Lv\\.(\\d+)");

    private EquipmentParsingUtil() {
    }

    public static EquipmentHistory parse(EquipmentDto dto) {
        EquipmentHistory equipment = new EquipmentHistory();
        equipment.setType(dto.getType());
        equipment.setName(dto.getName());
        equipment.setIcon(dto.getIcon());
        equipment.setGrade(dto.getGrade());
        equipment.setRefinement(parseRefinement(dto.getName()));

        if (dto.getTooltip() != null && !dto.getTooltip().isEmpty()) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject tooltip = (JSONObject) parser.parse(dto.getTooltip());
                parseTooltip(tooltip, equipment, dto.getType());
            } catch (Exception e) {
                log.warn("장비 툴팁 파싱 실패 - 장비: {}, 오류: {}", dto.getName(), e.getMessage());
            }
        }

        return equipment;
    }

    private static void parseTooltip(JSONObject tooltip, EquipmentHistory equipment, String type) {
        for (Object key : tooltip.keySet()) {
            Object elementObj = tooltip.get(key.toString());
            if (!(elementObj instanceof JSONObject)) continue;

            JSONObject element = (JSONObject) elementObj;
            String elementType = getStringValue(element, "type");

            if (elementType == null) continue;

            switch (elementType) {
                case "ItemTitle":
                    parseItemTitle(element, equipment);
                    break;
                case "ItemPartBox":
                    parseItemPartBox(element, equipment, type);
                    break;
                case "IndentStringGroup":
                    parseIndentStringGroup(element, equipment, type);
                    break;
                case "SingleTextBox":
                    parseSingleTextBox(element, equipment);
                    break;
                default:
                    break;
            }
        }
    }

    private static void parseItemTitle(JSONObject element, EquipmentHistory equipment) {
        JSONObject value = getJsonObject(element, "value");
        if (value == null) return;

        Object qualityObj = value.get("qualityValue");
        if (qualityObj != null) {
            try {
                int quality = Integer.parseInt(qualityObj.toString());
                equipment.setQuality(quality == -1 ? null : quality);
            } catch (NumberFormatException e) {
                log.debug("숫자 파싱 실패: {}", e.getMessage());
            }
        }

        String leftStr2 = getStringValue(value, "leftStr2");
        if (leftStr2 != null) {
            String plainText = stripHtml(leftStr2);
            Matcher matcher = ITEM_LEVEL_PATTERN.matcher(plainText);
            if (matcher.find()) {
                try {
                    String levelStr = matcher.group(1).replace(",", "");
                    equipment.setItemLevel(Integer.parseInt(levelStr));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private static void parseItemPartBox(JSONObject element, EquipmentHistory equipment, String type) {
        JSONObject value = getJsonObject(element, "value");
        if (value == null) return;

        String element000 = getStringValue(value, "Element_000");
        String element001 = getStringValue(value, "Element_001");

        if (element000 == null || element001 == null) return;

        String titleText = stripHtml(element000).trim();
        String contentText = stripHtml(element001).trim();

        if (titleText.contains("기본 효과")) {
            equipment.setBasicEffect(contentText);
        } else if (titleText.contains("추가 효과")) {
            equipment.setAdditionalEffect(contentText);
        } else if (titleText.contains("아크 패시브 포인트 효과")) {
            equipment.setArkPassiveEffect(contentText);
        } else if (titleText.contains("연마 효과") && isAccessory(type)) {
            equipment.setGrindingEffect(contentText);
        } else if (titleText.contains("팔찌 효과") && "팔찌".equals(type)) {
            equipment.setBraceletEffect(contentText);
        }
    }

    private static void parseIndentStringGroup(JSONObject element, EquipmentHistory equipment, String type) {
        if (!"어빌리티 스톤".equals(type)) return;

        JSONObject value = getJsonObject(element, "value");
        if (value == null) return;

        StringBuilder engravingBuilder = new StringBuilder();
        for (Object key : value.keySet()) {
            Object itemObj = value.get(key.toString());
            if (!(itemObj instanceof JSONObject)) continue;

            JSONObject item = (JSONObject) itemObj;
            String contentStr = getStringValue(item, "contentStr");
            if (contentStr == null) continue;

            String plainContent = stripHtml(contentStr).trim();
            Matcher matcher = ENGRAVING_PATTERN.matcher(plainContent);
            while (matcher.find()) {
                if (engravingBuilder.length() > 0) {
                    engravingBuilder.append(", ");
                }
                engravingBuilder.append(matcher.group(1).trim())
                        .append(" Lv.")
                        .append(matcher.group(2));
            }
        }

        if (engravingBuilder.length() > 0) {
            equipment.setEngravings(engravingBuilder.toString());
        }
    }

    private static void parseSingleTextBox(JSONObject element, EquipmentHistory equipment) {
        String value = getStringValue(element, "value");
        if (value == null) return;

        String plainText = stripHtml(value).trim();
        Matcher matcher = ADVANCED_REFINEMENT_PATTERN.matcher(plainText);
        if (matcher.find()) {
            try {
                equipment.setAdvancedRefinement(Integer.parseInt(matcher.group(1)));
            } catch (NumberFormatException e) {
                log.debug("숫자 파싱 실패: {}", e.getMessage());
            }
        }
    }

    static Integer parseRefinement(String name) {
        if (name == null) return null;
        Matcher matcher = REFINEMENT_PATTERN.matcher(name);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                log.debug("숫자 파싱 실패: {}", e.getMessage());
            }
        }
        return null;
    }

    static String stripHtml(String html) {
        if (html == null) return null;
        return HTML_TAG_PATTERN.matcher(html).replaceAll("").trim();
    }

    private static boolean isAccessory(String type) {
        return "목걸이".equals(type) || "귀걸이".equals(type) || "반지".equals(type);
    }

    private static String getStringValue(JSONObject obj, String key) {
        Object val = obj.get(key);
        return val != null ? val.toString() : null;
    }

    private static JSONObject getJsonObject(JSONObject obj, String key) {
        Object val = obj.get(key);
        if (val instanceof JSONObject) {
            return (JSONObject) val;
        }
        if (val instanceof String) {
            try {
                JSONParser parser = new JSONParser();
                Object parsed = parser.parse((String) val);
                if (parsed instanceof JSONObject) {
                    return (JSONObject) parsed;
                }
            } catch (Exception e) {
                log.warn("툴팁 내부 JSON 파싱 실패: {}", e.getMessage());
            }
        }
        return null;
    }
}
