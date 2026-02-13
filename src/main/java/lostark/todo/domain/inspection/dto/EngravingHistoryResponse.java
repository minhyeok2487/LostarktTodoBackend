// package lostark.todo.domain.inspection.dto;
// 
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lostark.todo.domain.inspection.entity.EngravingHistory;
// 
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class EngravingHistoryResponse {
// 
//     private String name;
//     private int level;
//     private String grade;
//     private Integer abilityStoneLevel;
//     private String description;
// 
//     public static EngravingHistoryResponse from(EngravingHistory entity) {
//         return EngravingHistoryResponse.builder()
//                 .name(entity.getName())
//                 .level(entity.getLevel())
//                 .grade(entity.getGrade())
//                 .abilityStoneLevel(entity.getAbilityStoneLevel())
//                 .description(entity.getDescription())
//                 .build();
//     }
// }
