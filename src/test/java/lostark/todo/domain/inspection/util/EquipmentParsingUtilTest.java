package lostark.todo.domain.inspection.util;

import lostark.todo.domain.inspection.dto.EquipmentDto;
import lostark.todo.domain.inspection.entity.EquipmentHistory;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("EquipmentParsingUtil 단위 테스트")
class EquipmentParsingUtilTest {

    // ── 공통 Tooltip JSON 빌더 헬퍼 ──

    /**
     * ItemTitle element JSON을 생성한다.
     * qualityValue=-1이면 품질이 없는 장비, leftStr2에 아이템 레벨 포함
     */
    private static String buildItemTitleElement(int qualityValue, int itemLevel) {
        JSONObject value = new JSONObject();
        value.put("qualityValue", qualityValue);
        value.put("leftStr2",
                "<FONT SIZE='12'>아이템 레벨 " + String.format("%,d", itemLevel) + " (티어 4)</FONT>");

        JSONObject element = new JSONObject();
        element.put("type", "ItemTitle");
        element.put("value", value.toJSONString());
        return element.toJSONString();
    }

    /**
     * ItemPartBox element JSON을 생성한다.
     */
    private static String buildItemPartBoxElement(String titleHtml, String contentHtml) {
        JSONObject value = new JSONObject();
        value.put("Element_000", titleHtml);
        value.put("Element_001", contentHtml);

        JSONObject element = new JSONObject();
        element.put("type", "ItemPartBox");
        element.put("value", value.toJSONString());
        return element.toJSONString();
    }

    /**
     * SingleTextBox element JSON을 생성한다 (상급 재련 정보).
     */
    private static String buildSingleTextBoxElement(String textHtml) {
        JSONObject element = new JSONObject();
        element.put("type", "SingleTextBox");
        element.put("value", textHtml);
        return element.toJSONString();
    }

    /**
     * IndentStringGroup element JSON을 생성한다 (어빌리티 스톤 각인).
     */
    private static String buildIndentStringGroupElement(String... engravingHtmls) {
        JSONObject value = new JSONObject();
        for (int i = 0; i < engravingHtmls.length; i++) {
            JSONObject item = new JSONObject();
            item.put("contentStr", engravingHtmls[i]);
            value.put("Element_" + String.format("%03d", i), item);
        }

        JSONObject element = new JSONObject();
        element.put("type", "IndentStringGroup");
        element.put("value", value.toJSONString());
        return element.toJSONString();
    }

    /**
     * 여러 element를 하나의 Tooltip JSON 문자열로 조합한다.
     */
    private static String buildTooltipJson(String... elementJsons) {
        JSONObject tooltip = new JSONObject();
        for (int i = 0; i < elementJsons.length; i++) {
            try {
                org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
                Object parsed = parser.parse(elementJsons[i]);
                tooltip.put("Element_" + String.format("%03d", i), parsed);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tooltip.toJSONString();
    }

    // ── 테스트 케이스 ──

    @Nested
    @DisplayName("무기 Tooltip 파싱")
    class WeaponTooltipParsing {

        @Test
        @DisplayName("무기 Tooltip에서 아이템 레벨 1755, 품질 100, 재련 +25, 상급 재련 40단계, 기본 효과, 추가 효과를 파싱한다")
        void parseWeaponTooltip_allFields() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(100, 1755),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>무기 공격력 +52719</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>추가 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>적에게 주는 피해 +2.40%</FONT>"
                    ),
                    buildSingleTextBoxElement(
                            "<FONT COLOR='#FFD200'>[상급 재련] 40단계</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+25 운명의 업화 롱 스태프", "icon_url", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("무기");
            assertThat(result.getName()).isEqualTo("+25 운명의 업화 롱 스태프");
            assertThat(result.getIcon()).isEqualTo("icon_url");
            assertThat(result.getGrade()).isEqualTo("에스더");
            assertThat(result.getRefinement()).isEqualTo(25);
            assertThat(result.getItemLevel()).isEqualTo(1755);
            assertThat(result.getQuality()).isEqualTo(100);
            assertThat(result.getBasicEffect()).isEqualTo("무기 공격력 +52719");
            assertThat(result.getAdditionalEffect()).isEqualTo("적에게 주는 피해 +2.40%");
            assertThat(result.getAdvancedRefinement()).isEqualTo(40);
        }

        @Test
        @DisplayName("상급 재련이 없는 무기 Tooltip을 파싱한다")
        void parseWeaponTooltip_noAdvancedRefinement() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(85, 1620),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>무기 공격력 +38000</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+21 운명의 파괴 롱 스태프", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getRefinement()).isEqualTo(21);
            assertThat(result.getItemLevel()).isEqualTo(1620);
            assertThat(result.getQuality()).isEqualTo(85);
            assertThat(result.getBasicEffect()).isEqualTo("무기 공격력 +38000");
            assertThat(result.getAdvancedRefinement()).isNull();
        }
    }

    @Nested
    @DisplayName("방어구 Tooltip 파싱")
    class ArmorTooltipParsing {

        @Test
        @DisplayName("방어구 Tooltip에서 기본 효과, 추가 효과, 아크 패시브 포인트 효과를 파싱한다")
        void parseArmorTooltip_withArkPassive() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(75, 1660),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>물리 방어력 +8500 마법 방어력 +7200 체력 +9500</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>추가 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>생명 활성력 +2800</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>아크 패시브 포인트 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>깨달음 +2</FONT>"
                    ),
                    buildSingleTextBoxElement(
                            "<FONT COLOR='#FFD200'>[상급 재련] 2단계</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "투구", "+25 운명의 업화 배틀 헬멧", "icon_url", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("투구");
            assertThat(result.getItemLevel()).isEqualTo(1660);
            assertThat(result.getQuality()).isEqualTo(75);
            assertThat(result.getRefinement()).isEqualTo(25);
            assertThat(result.getBasicEffect()).contains("물리 방어력");
            assertThat(result.getAdditionalEffect()).contains("생명 활성력");
            assertThat(result.getArkPassiveEffect()).isEqualTo("깨달음 +2");
            assertThat(result.getAdvancedRefinement()).isEqualTo(2);
        }

        @ParameterizedTest(name = "방어구 타입={0}")
        @ValueSource(strings = {"투구", "상의", "하의", "장갑", "어깨"})
        @DisplayName("모든 방어구 타입에 대해 파싱이 동작한다")
        void parseAllArmorTypes(String armorType) {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(80, 1640)
            );

            EquipmentDto dto = new EquipmentDto(
                    armorType, "+23 테스트 방어구", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo(armorType);
            assertThat(result.getItemLevel()).isEqualTo(1640);
            assertThat(result.getRefinement()).isEqualTo(23);
        }
    }

    @Nested
    @DisplayName("악세서리 Tooltip 파싱")
    class AccessoryTooltipParsing {

        @Test
        @DisplayName("목걸이 Tooltip에서 연마 효과와 아크 패시브 포인트 효과를 파싱한다")
        void parseNecklaceTooltip() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(90, 1660),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>체력 +14000 힘 +6700</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>추가 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명 +500 신속 +300</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>연마 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>공격력 +1.20%</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>아크 패시브 포인트 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>도약 +3</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "목걸이", "업화 목걸이", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("목걸이");
            assertThat(result.getQuality()).isEqualTo(90);
            assertThat(result.getBasicEffect()).contains("체력");
            assertThat(result.getAdditionalEffect()).contains("치명");
            assertThat(result.getGrindingEffect()).isEqualTo("공격력 +1.20%");
            assertThat(result.getArkPassiveEffect()).isEqualTo("도약 +3");
            assertThat(result.getRefinement()).isNull();
        }

        @ParameterizedTest(name = "악세서리 타입={0}")
        @ValueSource(strings = {"목걸이", "귀걸이", "반지"})
        @DisplayName("악세서리 타입에서만 연마 효과가 파싱된다")
        void parseGrindingEffectForAccessories(String accessoryType) {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(85, 1640),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>연마 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명타 피해 +4.00%</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    accessoryType, "테스트 악세서리", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getGrindingEffect()).isEqualTo("치명타 피해 +4.00%");
        }

        @Test
        @DisplayName("비악세서리 타입에서는 연마 효과가 파싱되지 않는다")
        void parseGrindingEffect_notParsedForNonAccessory() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(85, 1640),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>연마 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명타 피해 +4.00%</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "투구", "테스트 투구", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getGrindingEffect()).isNull();
        }
    }

    @Nested
    @DisplayName("어빌리티 스톤 Tooltip 파싱")
    class AbilityStoneTooltipParsing {

        @Test
        @DisplayName("어빌리티 스톤에서 각인을 추출한다")
        void parseAbilityStone_engravings() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0),
                    buildIndentStringGroupElement(
                            "<FONT COLOR='#FFFFAC'>원한 Lv.3</FONT>",
                            "<FONT COLOR='#FFFFAC'>슈퍼 차지 Lv.3</FONT>",
                            "<FONT COLOR='#FE2E2E'>방어력 감소 Lv.1</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "어빌리티 스톤", "빛나는 운명의 돌", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("어빌리티 스톤");
            assertThat(result.getEngravings()).contains("원한 Lv.3");
            assertThat(result.getEngravings()).contains("슈퍼 차지 Lv.3");
            assertThat(result.getEngravings()).contains("방어력 감소 Lv.1");
        }

        @Test
        @DisplayName("어빌리티 스톤이 아닌 장비에서는 각인이 파싱되지 않는다")
        void parseEngravings_notParsedForNonAbilityStone() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(80, 1640),
                    buildIndentStringGroupElement(
                            "<FONT COLOR='#FFFFAC'>원한 Lv.3</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getEngravings()).isNull();
        }
    }

    @Nested
    @DisplayName("팔찌 Tooltip 파싱")
    class BraceletTooltipParsing {

        @Test
        @DisplayName("팔찌 Tooltip에서 팔찌 효과를 파싱한다")
        void parseBraceletTooltip() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>팔찌 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명 +80 신속 +80 [상급] 정밀 [상급] 순환</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "팔찌", "화평석 팔찌", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("팔찌");
            assertThat(result.getBraceletEffect()).contains("치명 +80");
            assertThat(result.getBraceletEffect()).contains("순환");
        }

        @Test
        @DisplayName("팔찌가 아닌 타입에서는 팔찌 효과가 파싱되지 않는다")
        void parseBraceletEffect_notParsedForNonBracelet() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(85, 1640),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>팔찌 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명 +80</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "목걸이", "테스트 목걸이", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getBraceletEffect()).isNull();
        }
    }

    @Nested
    @DisplayName("품질 -1 처리")
    class QualityNegativeOneParsing {

        @Test
        @DisplayName("품질값이 -1이면 null로 설정한다")
        void parseQuality_negativeOne_setsNull() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0)
            );

            EquipmentDto dto = new EquipmentDto(
                    "어빌리티 스톤", "빛나는 운명의 돌", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getQuality()).isNull();
        }

        @Test
        @DisplayName("품질값이 0 이상이면 정상적으로 설정한다")
        void parseQuality_positive_setsValue() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(100, 1660)
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+25 테스트 무기", "icon_url", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getQuality()).isEqualTo(100);
        }

        @Test
        @DisplayName("품질값이 0이면 0으로 설정한다")
        void parseQuality_zero_setsZero() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(0, 1620)
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getQuality()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("HTML 태그 제거 테스트")
    class StripHtmlTest {

        @Test
        @DisplayName("HTML 태그가 정상적으로 제거된다")
        void stripHtml_removesAllTags() {
            // given / when
            String result = EquipmentParsingUtil.stripHtml(
                    "<FONT SIZE='12'><FONT COLOR='#A9D0F5'>아이템 레벨 1,660 (티어 4)</FONT></FONT>"
            );

            // then
            assertThat(result).isEqualTo("아이템 레벨 1,660 (티어 4)");
        }

        @Test
        @DisplayName("HTML 태그가 없으면 원본 문자열을 반환한다")
        void stripHtml_noTags_returnsOriginal() {
            // given / when
            String result = EquipmentParsingUtil.stripHtml("plain text");

            // then
            assertThat(result).isEqualTo("plain text");
        }

        @Test
        @DisplayName("null 입력이면 null을 반환한다")
        void stripHtml_null_returnsNull() {
            // given / when
            String result = EquipmentParsingUtil.stripHtml(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("중첩된 HTML 태그가 모두 제거된다")
        void stripHtml_nestedTags() {
            // given / when
            String result = EquipmentParsingUtil.stripHtml(
                    "<FONT><B><I>중첩 텍스트</I></B></FONT>"
            );

            // then
            assertThat(result).isEqualTo("중첩 텍스트");
        }

        @Test
        @DisplayName("빈 문자열 입력이면 빈 문자열을 반환한다")
        void stripHtml_empty_returnsEmpty() {
            // given / when
            String result = EquipmentParsingUtil.stripHtml("");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("이름에서 재련 단계 파싱")
    class ParseRefinementTest {

        @ParameterizedTest(name = "이름={0}, 기대값={1}")
        @CsvSource({
                "'+25 운명의 업화 롱 스태프', 25",
                "'+20 파괴의 장검', 20",
                "'+15 운명의 배틀 헬멧', 15",
                "'+1 시작의 무기', 1",
        })
        @DisplayName("재련 수치가 포함된 이름에서 올바르게 파싱한다")
        void parseRefinement_validNames(String name, int expected) {
            // when
            Integer result = EquipmentParsingUtil.parseRefinement(name);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("재련 수치가 없는 이름에서는 null을 반환한다")
        void parseRefinement_noRefinement_returnsNull() {
            // when
            Integer result = EquipmentParsingUtil.parseRefinement("화평석 팔찌");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 이름이면 null을 반환한다")
        void parseRefinement_null_returnsNull() {
            // when
            Integer result = EquipmentParsingUtil.parseRefinement(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 이름이면 null을 반환한다")
        void parseRefinement_empty_returnsNull() {
            // when
            Integer result = EquipmentParsingUtil.parseRefinement("");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("상급 재련 파싱")
    class AdvancedRefinementParsing {

        @ParameterizedTest(name = "상급 재련 {0}단계")
        @ValueSource(ints = {1, 2, 3, 5, 10, 20, 40})
        @DisplayName("상급 재련 다양한 단계가 정상적으로 파싱된다")
        void parseAdvancedRefinement_levels(int level) {
            // given
            String tooltip = buildTooltipJson(
                    buildSingleTextBoxElement(
                            "<FONT COLOR='#FFD200'>[상급 재련] " + level + "단계</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+25 테스트 무기", "icon_url", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getAdvancedRefinement()).isEqualTo(level);
        }

        @Test
        @DisplayName("SingleTextBox에 상급 재련 정보가 없으면 advancedRefinement가 null이다")
        void parseAdvancedRefinement_notPresent_isNull() {
            // given
            String tooltip = buildTooltipJson(
                    buildSingleTextBoxElement(
                            "<FONT COLOR='#FFD200'>일반 텍스트입니다</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getAdvancedRefinement()).isNull();
        }
    }

    @Nested
    @DisplayName("null/빈 Tooltip 예외 처리")
    class NullEmptyTooltipHandling {

        @Test
        @DisplayName("Tooltip이 null이면 Tooltip 관련 필드가 모두 null이다")
        void parse_nullTooltip_noException() {
            // given
            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", null
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("무기");
            assertThat(result.getName()).isEqualTo("+20 테스트 무기");
            assertThat(result.getRefinement()).isEqualTo(20);
            assertThat(result.getItemLevel()).isNull();
            assertThat(result.getQuality()).isNull();
            assertThat(result.getBasicEffect()).isNull();
            assertThat(result.getAdditionalEffect()).isNull();
            assertThat(result.getAdvancedRefinement()).isNull();
        }

        @Test
        @DisplayName("Tooltip이 빈 문자열이면 Tooltip 관련 필드가 모두 null이다")
        void parse_emptyTooltip_noException() {
            // given
            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", ""
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getItemLevel()).isNull();
            assertThat(result.getQuality()).isNull();
            assertThat(result.getBasicEffect()).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 JSON Tooltip이면 예외 없이 기본 정보만 설정된다")
        void parse_invalidJsonTooltip_noException() {
            // given
            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", "{{invalid json}}"
            );

            // when / then
            assertThatCode(() -> EquipmentParsingUtil.parse(dto))
                    .doesNotThrowAnyException();

            EquipmentHistory result = EquipmentParsingUtil.parse(dto);
            assertThat(result.getType()).isEqualTo("무기");
            assertThat(result.getName()).isEqualTo("+20 테스트 무기");
            assertThat(result.getRefinement()).isEqualTo(20);
        }

        @Test
        @DisplayName("Tooltip JSON의 value가 null인 element는 무시된다")
        void parse_nullValueInElement_noException() {
            // given
            JSONObject tooltip = new JSONObject();
            JSONObject element = new JSONObject();
            element.put("type", "ItemTitle");
            element.put("value", null);
            tooltip.put("Element_000", element);

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip.toJSONString()
            );

            // when / then
            assertThatCode(() -> EquipmentParsingUtil.parse(dto))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("특수 장비 파싱 (나침반, 부적, 보주)")
    class SpecialEquipmentParsing {

        @Test
        @DisplayName("나침반 타입 장비가 기본 정보만으로 파싱된다")
        void parseCompass() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0)
            );

            EquipmentDto dto = new EquipmentDto(
                    "나침반", "빛나는 운명의 나침반", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("나침반");
            assertThat(result.getName()).isEqualTo("빛나는 운명의 나침반");
            assertThat(result.getQuality()).isNull();
            assertThat(result.getRefinement()).isNull();
            assertThat(result.getGrindingEffect()).isNull();
            assertThat(result.getBraceletEffect()).isNull();
            assertThat(result.getEngravings()).isNull();
        }

        @Test
        @DisplayName("부적 타입 장비가 기본 정보만으로 파싱된다")
        void parseAmulet() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0)
            );

            EquipmentDto dto = new EquipmentDto(
                    "부적", "운명의 부적", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("부적");
            assertThat(result.getQuality()).isNull();
            assertThat(result.getGrindingEffect()).isNull();
            assertThat(result.getEngravings()).isNull();
        }

        @Test
        @DisplayName("보주 타입 장비도 예외 없이 처리된다")
        void parseJewelOrb_noException() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(-1, 0)
            );

            EquipmentDto dto = new EquipmentDto(
                    "보주", "빛나는 운명의 보주", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("보주");
            assertThat(result.getQuality()).isNull();
            assertThat(result.getRefinement()).isNull();
            assertThat(result.getGrindingEffect()).isNull();
            assertThat(result.getEngravings()).isNull();
        }
    }

    @Nested
    @DisplayName("아이템 레벨 파싱 세부 테스트")
    class ItemLevelParsing {

        @Test
        @DisplayName("쉼표가 포함된 아이템 레벨을 올바르게 파싱한다")
        void parseItemLevel_withComma() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(90, 1660)
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+25 테스트 무기", "icon_url", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getItemLevel()).isEqualTo(1660);
        }

        @Test
        @DisplayName("ItemTitle이 없는 Tooltip에서 아이템 레벨이 null이다")
        void parseItemLevel_noItemTitle_isNull() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>무기 공격력 +10000</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getItemLevel()).isNull();
            assertThat(result.getQuality()).isNull();
            assertThat(result.getBasicEffect()).isEqualTo("무기 공격력 +10000");
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarios {

        @Test
        @DisplayName("모든 필드가 포함된 완전한 무기 Tooltip을 파싱한다 (아이템 레벨 1755, 품질 100, 상재 40단계)")
        void parseCompleteWeaponTooltip() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(100, 1755),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>무기 공격력 +52719</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>추가 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>적에게 주는 피해 +2.40%</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>아크 패시브 포인트 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>진화 +5</FONT>"
                    ),
                    buildSingleTextBoxElement(
                            "<FONT COLOR='#FFD200'>[상급 재련] 40단계</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+25 운명의 업화 롱 스태프", "weapon_icon", "에스더", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("무기");
            assertThat(result.getName()).isEqualTo("+25 운명의 업화 롱 스태프");
            assertThat(result.getIcon()).isEqualTo("weapon_icon");
            assertThat(result.getGrade()).isEqualTo("에스더");
            assertThat(result.getRefinement()).isEqualTo(25);
            assertThat(result.getItemLevel()).isEqualTo(1755);
            assertThat(result.getQuality()).isEqualTo(100);
            assertThat(result.getBasicEffect()).isEqualTo("무기 공격력 +52719");
            assertThat(result.getAdditionalEffect()).isEqualTo("적에게 주는 피해 +2.40%");
            assertThat(result.getArkPassiveEffect()).isEqualTo("진화 +5");
            assertThat(result.getAdvancedRefinement()).isEqualTo(40);
            assertThat(result.getGrindingEffect()).isNull();
            assertThat(result.getBraceletEffect()).isNull();
            assertThat(result.getEngravings()).isNull();
        }

        @Test
        @DisplayName("모든 필드가 포함된 완전한 악세서리 Tooltip을 파싱한다")
        void parseCompleteAccessoryTooltip() {
            // given
            String tooltip = buildTooltipJson(
                    buildItemTitleElement(95, 1660),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>기본 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>체력 +14000</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>추가 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>치명 +500</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>연마 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>공격력 +2.40%</FONT>"
                    ),
                    buildItemPartBoxElement(
                            "<FONT COLOR='#A0A0A0'>아크 패시브 포인트 효과</FONT>",
                            "<FONT COLOR='#FFFFFF'>도약 +3</FONT>"
                    )
            );

            EquipmentDto dto = new EquipmentDto(
                    "귀걸이", "업화 귀걸이", "earring_icon", "유물", tooltip
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getType()).isEqualTo("귀걸이");
            assertThat(result.getQuality()).isEqualTo(95);
            assertThat(result.getBasicEffect()).isEqualTo("체력 +14000");
            assertThat(result.getAdditionalEffect()).isEqualTo("치명 +500");
            assertThat(result.getGrindingEffect()).isEqualTo("공격력 +2.40%");
            assertThat(result.getArkPassiveEffect()).isEqualTo("도약 +3");
            assertThat(result.getRefinement()).isNull();
            assertThat(result.getAdvancedRefinement()).isNull();
            assertThat(result.getBraceletEffect()).isNull();
            assertThat(result.getEngravings()).isNull();
        }

        @Test
        @DisplayName("알 수 없는 elementType은 무시된다")
        void parse_unknownElementType_ignored() {
            // given
            JSONObject tooltip = new JSONObject();
            JSONObject unknownElement = new JSONObject();
            unknownElement.put("type", "UnknownType");
            unknownElement.put("value", "some value");
            tooltip.put("Element_000", unknownElement);

            JSONObject titleElement = new JSONObject();
            titleElement.put("type", "ItemTitle");
            JSONObject titleValue = new JSONObject();
            titleValue.put("qualityValue", 80);
            titleValue.put("leftStr2", "아이템 레벨 1,640 (티어 4)");
            titleElement.put("value", titleValue);
            tooltip.put("Element_001", titleElement);

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip.toJSONString()
            );

            // when
            EquipmentHistory result = EquipmentParsingUtil.parse(dto);

            // then
            assertThat(result.getQuality()).isEqualTo(80);
            assertThat(result.getItemLevel()).isEqualTo(1640);
        }

        @Test
        @DisplayName("element가 JSONObject가 아닌 경우 무시된다")
        void parse_nonJsonObjectElement_ignored() {
            // given
            JSONObject tooltip = new JSONObject();
            tooltip.put("Element_000", "string_value_not_object");

            EquipmentDto dto = new EquipmentDto(
                    "무기", "+20 테스트 무기", "icon_url", "유물", tooltip.toJSONString()
            );

            // when / then
            assertThatCode(() -> EquipmentParsingUtil.parse(dto))
                    .doesNotThrowAnyException();

            EquipmentHistory result = EquipmentParsingUtil.parse(dto);
            assertThat(result.getType()).isEqualTo("무기");
        }
    }
}
