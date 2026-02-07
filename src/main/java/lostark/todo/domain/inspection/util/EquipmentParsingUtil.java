package lostark.todo.domain.inspection.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.inspection.dto.EquipmentDto;
import lostark.todo.domain.inspection.entity.EquipmentHistory;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EquipmentParsingUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
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
                JsonNode tooltip = MAPPER.readTree(dto.getTooltip());
                parseTooltip(tooltip, equipment, dto.getType());
            } catch (Exception e) {
                log.warn("장비 툴팁 파싱 실패 - 장비: {}, 오류: {}", dto.getName(), e.getMessage());
            }
        }

        return equipment;
    }

    private static void parseTooltip(JsonNode tooltip, EquipmentHistory equipment, String type) {
        Iterator<String> fieldNames = tooltip.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            JsonNode element = tooltip.get(key);
            if (!element.isObject()) continue;

            String elementType = getTextOrNull(element, "type");
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

    private static void parseItemTitle(JsonNode element, EquipmentHistory equipment) {
        JsonNode value = getJsonNode(element, "value");
        if (value == null) return;

        JsonNode qualityNode = value.get("qualityValue");
        if (qualityNode != null && !qualityNode.isNull()) {
            try {
                int quality = qualityNode.asInt();
                equipment.setQuality(quality == -1 ? null : quality);
            } catch (Exception e) {
                log.debug("숫자 파싱 실패: {}", e.getMessage());
            }
        }

        String leftStr2 = getTextOrNull(value, "leftStr2");
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

    private static void parseItemPartBox(JsonNode element, EquipmentHistory equipment, String type) {
        JsonNode value = getJsonNode(element, "value");
        if (value == null) return;

        String element000 = getTextOrNull(value, "Element_000");
        String element001 = getTextOrNull(value, "Element_001");

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

    private static void parseIndentStringGroup(JsonNode element, EquipmentHistory equipment, String type) {
        if (!"어빌리티 스톤".equals(type)) return;

        JsonNode value = getJsonNode(element, "value");
        if (value == null) return;

        StringBuilder engravingBuilder = new StringBuilder();
        Iterator<String> fieldNames = value.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            JsonNode item = value.get(key);
            if (!item.isObject()) continue;

            String contentStr = getTextOrNull(item, "contentStr");
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

    private static void parseSingleTextBox(JsonNode element, EquipmentHistory equipment) {
        String value = getTextOrNull(element, "value");
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

    private static String getTextOrNull(JsonNode node, String field) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) return null;
        return child.asText();
    }

    /**
     * element의 "value" 필드를 JsonNode로 반환.
     * value가 문자열인 경우 JSON으로 한번 더 파싱.
     */
    private static JsonNode getJsonNode(JsonNode node, String field) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) return null;
        if (child.isObject()) return child;
        if (child.isTextual()) {
            try {
                JsonNode parsed = MAPPER.readTree(child.asText());
                if (parsed.isObject()) return parsed;
            } catch (Exception e) {
                log.warn("툴팁 내부 JSON 파싱 실패: {}", e.getMessage());
            }
        }
        return null;
    }
}
