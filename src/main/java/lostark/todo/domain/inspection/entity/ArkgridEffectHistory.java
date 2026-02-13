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
// @Table(name = "arkgrid_effect_history", indexes = {
//         @Index(name = "idx_arkgrid_history", columnList = "combat_power_history_id")
// })
// public class ArkgridEffectHistory extends BaseTimeEntity {
// 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "arkgrid_effect_history_id")
//     private long id;
// 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "combat_power_history_id")
//     @JsonBackReference
//     private CombatPowerHistory combatPowerHistory;
// 
//     @Column(nullable = false)
//     private String effectName;
// 
//     private int effectLevel;
// 
//     @Column(length = 1000)
//     private String effectTooltip;
// }
