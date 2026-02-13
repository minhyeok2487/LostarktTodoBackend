// package lostark.todo.domain.inspection.entity;
// 
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import lombok.*;
// import lostark.todo.global.entity.BaseTimeEntity;
// 
// import javax.persistence.*;
// 
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Entity
// @Table(name = "card_set_effect_history", indexes = {
//         @Index(name = "idx_card_set_effect_history", columnList = "combat_power_history_id")
// })
// public class CardSetEffectHistory extends BaseTimeEntity {
// 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "card_set_effect_history_id")
//     private long id;
// 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "combat_power_history_id")
//     @JsonBackReference
//     private CombatPowerHistory combatPowerHistory;
// 
//     @Column(nullable = false)
//     private String name;
// 
//     @Column(columnDefinition = "TEXT")
//     private String description;
// }
