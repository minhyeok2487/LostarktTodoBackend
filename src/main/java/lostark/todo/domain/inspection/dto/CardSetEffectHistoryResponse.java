// package lostark.todo.domain.inspection.dto;
// 
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lostark.todo.domain.inspection.entity.CardSetEffectHistory;
// 
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class CardSetEffectHistoryResponse {
// 
//     private String name;
//     private String description;
// 
//     public static CardSetEffectHistoryResponse from(CardSetEffectHistory entity) {
//         return CardSetEffectHistoryResponse.builder()
//                 .name(entity.getName())
//                 .description(entity.getDescription())
//                 .build();
//     }
// }
