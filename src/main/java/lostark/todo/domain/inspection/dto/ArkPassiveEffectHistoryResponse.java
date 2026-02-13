// package lostark.todo.domain.inspection.dto;
// 
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lostark.todo.domain.inspection.entity.ArkPassiveEffectHistory;
// 
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class ArkPassiveEffectHistoryResponse {
// 
//     private String category;
//     private String effectName;
//     private String icon;
//     private int tier;
//     private int level;
// 
//     public static ArkPassiveEffectHistoryResponse from(ArkPassiveEffectHistory entity) {
//         return ArkPassiveEffectHistoryResponse.builder()
//                 .category(entity.getCategory())
//                 .effectName(entity.getEffectName())
//                 .icon(entity.getIcon())
//                 .tier(entity.getTier())
//                 .level(entity.getLevel())
//                 .build();
//     }
// }
