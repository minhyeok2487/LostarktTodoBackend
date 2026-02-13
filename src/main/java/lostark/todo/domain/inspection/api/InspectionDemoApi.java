// package lostark.todo.domain.inspection.api;
// 
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import lombok.extern.slf4j.Slf4j;
// import lostark.todo.domain.inspection.dto.*;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// 
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// 
// @RestController
// @Slf4j
// @RequestMapping("/api/v1/inspection/demo")
// @Api(tags = {"군장검사 데모 API"})
// public class InspectionDemoApi {
// 
//     @ApiOperation(value = "군장검사 데모 데이터 조회 (인증 불필요)")
//     @GetMapping
//     public ResponseEntity<InspectionDashboardResponse> getDemoData() {
//         return ResponseEntity.ok(buildDemoData());
//     }
// 
//     private InspectionDashboardResponse buildDemoData() {
//         InspectionCharacterResponse character = InspectionCharacterResponse.builder()
//                 .id(9999)
//                 .characterName("데모캐릭터")
//                 .serverName("루페온")
//                 .characterClassName("버서커")
//                 .characterImage(null)
//                 .itemLevel(1640.00)
//                 .combatPower(98520.5)
//                 .title("절대자의 군주")
//                 .guildName("데모길드")
//                 .townName("비탄의 섬")
//                 .townLevel(60)
//                 .expeditionLevel(250)
//                 .noChangeThreshold(3)
//                 .isActive(true)
//                 .createdDate(LocalDateTime.now().minusDays(30))
//                 .previousCombatPower(97800.0)
//                 .combatPowerChange(720.5)
//                 .previousItemLevel(1640.00)
//                 .itemLevelChange(0.0)
//                 .unchangedDays(0)
//                 .build();
// 
//         LocalDate today = LocalDate.now();
//         LocalDate yesterday = today.minusDays(1);
// 
//         CombatPowerHistoryResponse todayHistory = buildTodayHistory(today);
//         CombatPowerHistoryResponse yesterdayHistory = buildYesterdayHistory(yesterday);
// 
//         return InspectionDashboardResponse.builder()
//                 .character(character)
//                 .histories(Arrays.asList(todayHistory, yesterdayHistory))
//                 .build();
//     }
// 
//     private CombatPowerHistoryResponse buildTodayHistory(LocalDate date) {
//         return CombatPowerHistoryResponse.builder()
//                 .id(1)
//                 .recordDate(date)
//                 .combatPower(98520.5)
//                 .itemLevel(1640.00)
//                 .statsJson(null)
//                 .townName("비탄의 섬")
//                 .townLevel(60)
//                 .equipments(buildTodayEquipments())
//                 .engravings(buildEngravings())
//                 .gems(buildGems())
//                 .cards(buildCards())
//                 .cardSetEffects(buildCardSetEffects())
//                 .arkPassivePoints(buildArkPassivePoints())
//                 .arkPassiveEffects(buildArkPassiveEffects())
//                 .arkPassiveTitle("진화의 비상")
//                 .arkgridEffects(buildArkgridEffects())
//                 .build();
//     }
// 
//     private CombatPowerHistoryResponse buildYesterdayHistory(LocalDate date) {
//         return CombatPowerHistoryResponse.builder()
//                 .id(2)
//                 .recordDate(date)
//                 .combatPower(97800.0)
//                 .itemLevel(1640.00)
//                 .statsJson(null)
//                 .townName("비탄의 섬")
//                 .townLevel(60)
//                 .equipments(buildYesterdayEquipments())
//                 .engravings(buildEngravings())
//                 .gems(buildYesterdayGems())
//                 .cards(buildCards())
//                 .cardSetEffects(buildCardSetEffects())
//                 .arkPassivePoints(buildYesterdayArkPassivePoints())
//                 .arkPassiveEffects(buildArkPassiveEffects())
//                 .arkPassiveTitle("진화의 비상")
//                 .arkgridEffects(Collections.emptyList())
//                 .build();
//     }
// 
//     // ===== 오늘 장비 (무기 +25, 상의 품질 97) =====
//     private List<EquipmentHistoryResponse> buildTodayEquipments() {
//         return Arrays.asList(
//                 // 방어구
//                 EquipmentHistoryResponse.builder()
//                         .type("투구").name("사멸의 환영 투구").icon(null).grade("고대")
//                         .itemLevel(1640).quality(85).refinement(22).advancedRefinement(1)
//                         .basicEffect("물리 방어력 +14520\n마법 방어력 +13280\n체력 +8500")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("어깨").name("사멸의 환영 견갑").icon(null).grade("고대")
//                         .itemLevel(1640).quality(90).refinement(22).advancedRefinement(2)
//                         .basicEffect("물리 방어력 +15100\n마법 방어력 +14200\n체력 +9200")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("상의").name("사멸의 환영 상의").icon(null).grade("고대")
//                         .itemLevel(1640).quality(97).refinement(23).advancedRefinement(3)
//                         .basicEffect("물리 방어력 +18200\n마법 방어력 +17500\n체력 +12000")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("하의").name("사멸의 환영 하의").icon(null).grade("고대")
//                         .itemLevel(1640).quality(88).refinement(22).advancedRefinement(1)
//                         .basicEffect("물리 방어력 +16800\n마법 방어력 +15900\n체력 +10500")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("장갑").name("사멸의 환영 장갑").icon(null).grade("고대")
//                         .itemLevel(1640).quality(92).refinement(22).advancedRefinement(2)
//                         .basicEffect("물리 방어력 +13800\n마법 방어력 +12500\n체력 +7800")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 무기 (+24 -> +25 변화)
//                 EquipmentHistoryResponse.builder()
//                         .type("무기").name("사멸의 환영 대검").icon(null).grade("고대")
//                         .itemLevel(1640).quality(95).refinement(25).advancedRefinement(4)
//                         .basicEffect("무기 공격력 +48500")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 악세서리
//                 EquipmentHistoryResponse.builder()
//                         .type("목걸이").name("고대 전설의 목걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(100).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +560\n특화 +340")
//                         .additionalEffect("추가 피해 +2.10%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("적에게 주는 피해 +1.60%\n추가 피해 +2.30%\n치명타 적중 +480")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("귀걸이").name("고대 전설의 귀걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(95).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +420")
//                         .additionalEffect("무기 공격력 +1.80%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("공격력 +960\n적에게 주는 피해 +0.80%\n최대 생명력 +3200")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("귀걸이").name("고대 전설의 귀걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(88).refinement(null).advancedRefinement(null)
//                         .basicEffect("특화 +420")
//                         .additionalEffect("무기 공격력 +1.50%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("치명타 피해 +4.50%\n추가 피해 +1.20%\n최대 마나 +1500")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("반지").name("고대 전설의 반지").icon(null).grade("고대")
//                         .itemLevel(null).quality(92).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +310")
//                         .additionalEffect("공격력 +1.20%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("적에게 주는 피해 +1.20%\n치명타 적중 +360\n공격력 +720")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("반지").name("고대 전설의 반지").icon(null).grade("고대")
//                         .itemLevel(null).quality(90).refinement(null).advancedRefinement(null)
//                         .basicEffect("특화 +310")
//                         .additionalEffect("공격력 +1.00%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("추가 피해 +1.80%\n적에게 주는 피해 +0.80%\n전투 중 생명력 회복량 +50")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 어빌리티 스톤
//                 EquipmentHistoryResponse.builder()
//                         .type("어빌리티 스톤").name("찬란한 태양의 돌").icon(null).grade("고대")
//                         .itemLevel(null).quality(null).refinement(null).advancedRefinement(null)
//                         .basicEffect("체력 +21326")
//                         .additionalEffect(null).arkPassiveEffect(null)
//                         .grindingEffect(null).braceletEffect(null)
//                         .engravings("원한 Lv.3, 슈퍼 차지 Lv.3")
//                         .build(),
//                 // 팔찌
//                 EquipmentHistoryResponse.builder()
//                         .type("팔찌").name("찬란한 비상의 팔찌").icon(null).grade("고대")
//                         .itemLevel(null).quality(null).refinement(null).advancedRefinement(null)
//                         .basicEffect(null).additionalEffect(null).arkPassiveEffect(null)
//                         .grindingEffect(null)
//                         .braceletEffect("치명 +80\n특화 +120\n[세밀한] 치명타 피해 +3.00%\n[강타] 적에게 주는 피해 +1.60%")
//                         .engravings(null)
//                         .build()
//         );
//     }
// 
//     // ===== 어제 장비 (무기 +24, 상의 품질 92) =====
//     private List<EquipmentHistoryResponse> buildYesterdayEquipments() {
//         return Arrays.asList(
//                 EquipmentHistoryResponse.builder()
//                         .type("투구").name("사멸의 환영 투구").icon(null).grade("고대")
//                         .itemLevel(1640).quality(85).refinement(22).advancedRefinement(1)
//                         .basicEffect("물리 방어력 +14520\n마법 방어력 +13280\n체력 +8500")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("어깨").name("사멸의 환영 견갑").icon(null).grade("고대")
//                         .itemLevel(1640).quality(90).refinement(22).advancedRefinement(2)
//                         .basicEffect("물리 방어력 +15100\n마법 방어력 +14200\n체력 +9200")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 상의: 품질 92 -> 97 (오늘 변화)
//                 EquipmentHistoryResponse.builder()
//                         .type("상의").name("사멸의 환영 상의").icon(null).grade("고대")
//                         .itemLevel(1640).quality(92).refinement(23).advancedRefinement(3)
//                         .basicEffect("물리 방어력 +18200\n마법 방어력 +17500\n체력 +12000")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("하의").name("사멸의 환영 하의").icon(null).grade("고대")
//                         .itemLevel(1640).quality(88).refinement(22).advancedRefinement(1)
//                         .basicEffect("물리 방어력 +16800\n마법 방어력 +15900\n체력 +10500")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("장갑").name("사멸의 환영 장갑").icon(null).grade("고대")
//                         .itemLevel(1640).quality(92).refinement(22).advancedRefinement(2)
//                         .basicEffect("물리 방어력 +13800\n마법 방어력 +12500\n체력 +7800")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 무기: +24 -> +25 (오늘 변화)
//                 EquipmentHistoryResponse.builder()
//                         .type("무기").name("사멸의 환영 대검").icon(null).grade("고대")
//                         .itemLevel(1640).quality(95).refinement(24).advancedRefinement(4)
//                         .basicEffect("무기 공격력 +45200")
//                         .additionalEffect(null).arkPassiveEffect(null).grindingEffect(null)
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 악세서리 (목걸이 동일, 귀걸이1 연마+품질 변경, 반지1 품질 변경)
//                 EquipmentHistoryResponse.builder()
//                         .type("목걸이").name("고대 전설의 목걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(100).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +560\n특화 +340")
//                         .additionalEffect("추가 피해 +2.10%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("적에게 주는 피해 +1.60%\n추가 피해 +2.30%\n치명타 적중 +480")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 귀걸이1: 어제 품질 82, 연마 다름 → 오늘 품질 95, 연마 변경
//                 EquipmentHistoryResponse.builder()
//                         .type("귀걸이").name("고대 전설의 귀걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(82).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +420")
//                         .additionalEffect("무기 공격력 +1.50%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("공격력 +720\n추가 피해 +0.55%\n최대 생명력 +2400")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("귀걸이").name("고대 전설의 귀걸이").icon(null).grade("고대")
//                         .itemLevel(null).quality(88).refinement(null).advancedRefinement(null)
//                         .basicEffect("특화 +420")
//                         .additionalEffect("무기 공격력 +1.50%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("치명타 피해 +4.50%\n추가 피해 +1.20%\n최대 마나 +1500")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 // 반지1: 어제 품질 85 → 오늘 품질 92
//                 EquipmentHistoryResponse.builder()
//                         .type("반지").name("고대 전설의 반지").icon(null).grade("고대")
//                         .itemLevel(null).quality(85).refinement(null).advancedRefinement(null)
//                         .basicEffect("치명 +310")
//                         .additionalEffect("공격력 +1.20%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("적에게 주는 피해 +1.20%\n치명타 적중 +360\n공격력 +720")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("반지").name("고대 전설의 반지").icon(null).grade("고대")
//                         .itemLevel(null).quality(90).refinement(null).advancedRefinement(null)
//                         .basicEffect("특화 +310")
//                         .additionalEffect("공격력 +1.00%")
//                         .arkPassiveEffect(null)
//                         .grindingEffect("추가 피해 +1.80%\n적에게 주는 피해 +0.80%\n전투 중 생명력 회복량 +50")
//                         .braceletEffect(null).engravings(null)
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("어빌리티 스톤").name("준엄한 비상의 돌").icon(null).grade("고대")
//                         .itemLevel(null).quality(null).refinement(null).advancedRefinement(null)
//                         .basicEffect("체력 +18000")
//                         .additionalEffect(null).arkPassiveEffect(null)
//                         .grindingEffect(null).braceletEffect(null)
//                         .engravings("타격의 대가 Lv.1, 아드레날린 Lv.4")
//                         .build(),
//                 EquipmentHistoryResponse.builder()
//                         .type("팔찌").name("찬란한 비상의 팔찌").icon(null).grade("고대")
//                         .itemLevel(null).quality(null).refinement(null).advancedRefinement(null)
//                         .basicEffect(null).additionalEffect(null).arkPassiveEffect(null)
//                         .grindingEffect(null)
//                         .braceletEffect("치명 +80\n특화 +120\n[세밀한] 치명타 피해 +3.00%\n[강타] 적에게 주는 피해 +1.60%")
//                         .engravings(null)
//                         .build()
//         );
//     }
// 
//     private List<EngravingHistoryResponse> buildEngravings() {
//         return Arrays.asList(
//                 EngravingHistoryResponse.builder().name("원한").level(4).grade("유물").abilityStoneLevel(3).description(null).build(),
//                 EngravingHistoryResponse.builder().name("슈퍼 차지").level(4).grade("유물").abilityStoneLevel(3).description(null).build(),
//                 EngravingHistoryResponse.builder().name("기습의 대가").level(3).grade("유물").abilityStoneLevel(null).description(null).build(),
//                 EngravingHistoryResponse.builder().name("아드레날린").level(3).grade("유물").abilityStoneLevel(null).description(null).build(),
//                 EngravingHistoryResponse.builder().name("저주받은 인형").level(2).grade("유물").abilityStoneLevel(null).description(null).build(),
//                 EngravingHistoryResponse.builder().name("버서커의 비기").level(1).grade("전설").abilityStoneLevel(null).description(null).build()
//         );
//     }
// 
//     // 오늘 보석: 소드 스톰 Lv.10, 스트라이크 웨이브 Lv.10, 템페스트 슬래시 Lv.10 (어제 대비 승급)
//     private List<GemHistoryResponse> buildGems() {
//         return Arrays.asList(
//                 GemHistoryResponse.builder().skillName("소드 스톰").gemSlot(0).skillIcon(null).gemIcon(null).level(10).grade("고대").description("피해 +40%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("소드 스톰").gemSlot(1).skillIcon(null).gemIcon(null).level(10).grade("고대").description("재사용 대기시간 -20%").gemOption("재사용").build(),
//                 GemHistoryResponse.builder().skillName("스트라이크 웨이브").gemSlot(2).skillIcon(null).gemIcon(null).level(10).grade("고대").description("피해 +40%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("스트라이크 웨이브").gemSlot(3).skillIcon(null).gemIcon(null).level(10).grade("고대").description("재사용 대기시간 -20%").gemOption("재사용").build(),
//                 GemHistoryResponse.builder().skillName("템페스트 슬래시").gemSlot(4).skillIcon(null).gemIcon(null).level(10).grade("고대").description("피해 +40%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("템페스트 슬래시").gemSlot(5).skillIcon(null).gemIcon(null).level(10).grade("고대").description("재사용 대기시간 -20%").gemOption("재사용").build()
//         );
//     }
// 
//     // 어제 보석: 템페스트 슬래시 Lv.8 전설, 나머지 동일
//     private List<GemHistoryResponse> buildYesterdayGems() {
//         return Arrays.asList(
//                 GemHistoryResponse.builder().skillName("소드 스톰").gemSlot(0).skillIcon(null).gemIcon(null).level(10).grade("고대").description("피해 +40%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("소드 스톰").gemSlot(1).skillIcon(null).gemIcon(null).level(10).grade("고대").description("재사용 대기시간 -20%").gemOption("재사용").build(),
//                 GemHistoryResponse.builder().skillName("스트라이크 웨이브").gemSlot(2).skillIcon(null).gemIcon(null).level(10).grade("고대").description("피해 +40%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("스트라이크 웨이브").gemSlot(3).skillIcon(null).gemIcon(null).level(10).grade("고대").description("재사용 대기시간 -20%").gemOption("재사용").build(),
//                 GemHistoryResponse.builder().skillName("템페스트 슬래시").gemSlot(4).skillIcon(null).gemIcon(null).level(8).grade("전설").description("피해 +28%").gemOption("피해").build(),
//                 GemHistoryResponse.builder().skillName("템페스트 슬래시").gemSlot(5).skillIcon(null).gemIcon(null).level(8).grade("전설").description("재사용 대기시간 -14%").gemOption("재사용").build()
//         );
//     }
// 
//     private List<CardHistoryResponse> buildCards() {
//         return Arrays.asList(
//                 CardHistoryResponse.builder().slot(0).name("카제로스").icon(null).awakeCount(5).awakeTotal(5).grade("전설").build(),
//                 CardHistoryResponse.builder().slot(1).name("라우리엘").icon(null).awakeCount(5).awakeTotal(5).grade("전설").build(),
//                 CardHistoryResponse.builder().slot(2).name("니나브").icon(null).awakeCount(5).awakeTotal(5).grade("전설").build(),
//                 CardHistoryResponse.builder().slot(3).name("아제나 & 이난나").icon(null).awakeCount(5).awakeTotal(5).grade("전설").build(),
//                 CardHistoryResponse.builder().slot(4).name("바칼").icon(null).awakeCount(4).awakeTotal(5).grade("전설").build()
//         );
//     }
// 
//     private List<CardSetEffectHistoryResponse> buildCardSetEffects() {
//         return Arrays.asList(
//                 CardSetEffectHistoryResponse.builder().name("세상을 구하는 빛").description("암속성 피해 감소 +30%").build()
//         );
//     }
// 
//     private List<ArkPassivePointHistoryResponse> buildArkPassivePoints() {
//         return Arrays.asList(
//                 ArkPassivePointHistoryResponse.builder().name("진화").value(75).description("진화 포인트 75").build(),
//                 ArkPassivePointHistoryResponse.builder().name("깨달음").value(65).description("깨달음 포인트 65").build(),
//                 ArkPassivePointHistoryResponse.builder().name("도약").value(50).description("도약 포인트 50").build()
//         );
//     }
// 
//     private List<ArkPassivePointHistoryResponse> buildYesterdayArkPassivePoints() {
//         return Arrays.asList(
//                 ArkPassivePointHistoryResponse.builder().name("진화").value(72).description(null).build(),
//                 ArkPassivePointHistoryResponse.builder().name("깨달음").value(65).description(null).build(),
//                 ArkPassivePointHistoryResponse.builder().name("도약").value(48).description(null).build()
//         );
//     }
// 
//     private List<ArkPassiveEffectHistoryResponse> buildArkPassiveEffects() {
//         return Arrays.asList(
//                 ArkPassiveEffectHistoryResponse.builder().category("진화").effectName("공격력 증가").icon(null).tier(1).level(10).build(),
//                 ArkPassiveEffectHistoryResponse.builder().category("진화").effectName("치명타 확률").icon(null).tier(2).level(8).build(),
//                 ArkPassiveEffectHistoryResponse.builder().category("깨달음").effectName("피해량 증가").icon(null).tier(1).level(10).build(),
//                 ArkPassiveEffectHistoryResponse.builder().category("깨달음").effectName("스킬 재사용").icon(null).tier(2).level(7).build(),
//                 ArkPassiveEffectHistoryResponse.builder().category("도약").effectName("추가 피해").icon(null).tier(1).level(10).build()
//         );
//     }
// 
//     private List<ArkgridEffectResponse> buildArkgridEffects() {
//         return Arrays.asList(
//                 ArkgridEffectResponse.builder().effectName("공격력 강화").effectLevel(5).effectTooltip("공격력이 5% 증가합니다.").build(),
//                 ArkgridEffectResponse.builder().effectName("치명 강화").effectLevel(3).effectTooltip("치명타 확률이 3% 증가합니다.").build(),
//                 ArkgridEffectResponse.builder().effectName("특화 강화").effectLevel(4).effectTooltip("특화 효과가 4% 증가합니다.").build()
//         );
//     }
// }
