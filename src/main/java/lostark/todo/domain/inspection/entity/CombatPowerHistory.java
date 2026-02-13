// package lostark.todo.domain.inspection.entity;
// 
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import lombok.*;
// import lostark.todo.global.entity.BaseTimeEntity;
// 
// import org.hibernate.annotations.BatchSize;
// 
// import javax.persistence.*;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// 
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Entity
// @Table(name = "combat_power_history", indexes = {
//         @Index(name = "idx_history_char_date", columnList = "inspection_character_id, record_date")
// }, uniqueConstraints = {
//         @UniqueConstraint(name = "uk_history_char_date", columnNames = {"inspection_character_id", "record_date"})
// })
// public class CombatPowerHistory extends BaseTimeEntity {
// 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "combat_power_history_id")
//     private long id;
// 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "inspection_character_id")
//     @JsonBackReference
//     private InspectionCharacter inspectionCharacter;
// 
//     @Column(name = "record_date", nullable = false)
//     private LocalDate recordDate;
// 
//     @Column(nullable = false)
//     private double combatPower;
// 
//     private double itemLevel;
// 
//     @Column(length = 500)
//     private String characterImage;
// 
//     @Column(columnDefinition = "TEXT")
//     private String statsJson;
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 20)
//     @Builder.Default
//     private List<ArkgridEffectHistory> arkgridEffects = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 30)
//     @Builder.Default
//     private List<EquipmentHistory> equipments = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 20)
//     @Builder.Default
//     private List<EngravingHistory> engravings = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 20)
//     @Builder.Default
//     private List<CardHistory> cards = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 10)
//     @Builder.Default
//     private List<CardSetEffectHistory> cardSetEffects = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 20)
//     @Builder.Default
//     private List<GemHistory> gems = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 10)
//     @Builder.Default
//     private List<ArkPassivePointHistory> arkPassivePoints = new ArrayList<>();
// 
//     @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @BatchSize(size = 30)
//     @Builder.Default
//     private List<ArkPassiveEffectHistory> arkPassiveEffects = new ArrayList<>();
// 
//     private String arkPassiveTitle;
// 
//     private String townName;
// 
//     private Integer townLevel;
// 
//     public void updateData(double combatPower, double itemLevel, String characterImage, String statsJson) {
//         this.combatPower = combatPower;
//         this.itemLevel = itemLevel;
//         this.characterImage = characterImage;
//         this.statsJson = statsJson;
//     }
// 
//     public void replaceArkgridEffects(List<ArkgridEffectHistory> newEffects) {
//         this.arkgridEffects.clear();
//         newEffects.forEach(effect -> {
//             effect.setCombatPowerHistory(this);
//             this.arkgridEffects.add(effect);
//         });
//     }
// 
//     public void replaceEquipments(List<EquipmentHistory> newEquipments) {
//         this.equipments.clear();
//         newEquipments.forEach(equipment -> {
//             equipment.setCombatPowerHistory(this);
//             this.equipments.add(equipment);
//         });
//     }
// 
//     public void replaceEngravings(List<EngravingHistory> newEngravings) {
//         this.engravings.clear();
//         newEngravings.forEach(engraving -> {
//             engraving.setCombatPowerHistory(this);
//             this.engravings.add(engraving);
//         });
//     }
// 
//     public void replaceCards(List<CardHistory> newCards) {
//         this.cards.clear();
//         newCards.forEach(card -> {
//             card.setCombatPowerHistory(this);
//             this.cards.add(card);
//         });
//     }
// 
//     public void replaceCardSetEffects(List<CardSetEffectHistory> newEffects) {
//         this.cardSetEffects.clear();
//         newEffects.forEach(effect -> {
//             effect.setCombatPowerHistory(this);
//             this.cardSetEffects.add(effect);
//         });
//     }
// 
//     public void replaceGems(List<GemHistory> newGems) {
//         this.gems.clear();
//         newGems.forEach(gem -> {
//             gem.setCombatPowerHistory(this);
//             this.gems.add(gem);
//         });
//     }
// 
//     public void replaceArkPassivePoints(List<ArkPassivePointHistory> newPoints) {
//         this.arkPassivePoints.clear();
//         newPoints.forEach(point -> {
//             point.setCombatPowerHistory(this);
//             this.arkPassivePoints.add(point);
//         });
//     }
// 
//     public void replaceArkPassiveEffects(List<ArkPassiveEffectHistory> newEffects) {
//         this.arkPassiveEffects.clear();
//         newEffects.forEach(effect -> {
//             effect.setCombatPowerHistory(this);
//             this.arkPassiveEffects.add(effect);
//         });
//     }
// }
