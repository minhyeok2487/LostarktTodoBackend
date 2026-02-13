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
// @Table(name = "equipment_history", indexes = {
//         @Index(name = "idx_equipment_history", columnList = "combat_power_history_id")
// })
// public class EquipmentHistory extends BaseTimeEntity {
// 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "equipment_history_id")
//     private long id;
// 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "combat_power_history_id")
//     @JsonBackReference
//     private CombatPowerHistory combatPowerHistory;
// 
//     @Column(nullable = false)
//     private String type;
// 
//     @Column(nullable = false)
//     private String name;
// 
//     @Column(length = 500)
//     private String icon;
// 
//     private String grade;
// 
//     private Integer itemLevel;
// 
//     private Integer quality;
// 
//     private Integer refinement;
// 
//     private Integer advancedRefinement;
// 
//     @Column(columnDefinition = "TEXT")
//     private String basicEffect;
// 
//     @Column(columnDefinition = "TEXT")
//     private String additionalEffect;
// 
//     private String arkPassiveEffect;
// 
//     @Column(columnDefinition = "TEXT")
//     private String grindingEffect;
// 
//     @Column(columnDefinition = "TEXT")
//     private String braceletEffect;
// 
//     @Column(length = 1000)
//     private String engravings;
// }
