// package lostark.todo.domain.inspection.entity;
// 
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import lombok.*;
// import lostark.todo.domain.member.entity.Member;
// import lostark.todo.global.entity.BaseTimeEntity;
// import org.hibernate.annotations.ColumnDefault;
// 
// import javax.persistence.*;
// import java.util.ArrayList;
// import java.util.List;
// 
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Entity
// @Table(name = "inspection_character", indexes = {
//         @Index(name = "idx_inspection_member", columnList = "member_id")
// }, uniqueConstraints = {
//         @UniqueConstraint(name = "uk_inspection_member_char", columnNames = {"member_id", "character_name"})
// })
// public class InspectionCharacter extends BaseTimeEntity {
// 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "inspection_character_id")
//     private long id;
// 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "member_id")
//     @JsonBackReference
//     private Member member;
// 
//     @Column(name = "character_name", nullable = false)
//     private String characterName;
// 
//     private String serverName;
// 
//     private String characterClassName;
// 
//     @Column(length = 500)
//     private String characterImage;
// 
//     private double itemLevel;
// 
//     private double combatPower;
// 
//     private String title;
// 
//     private String guildName;
// 
//     private String townName;
// 
//     private Integer townLevel;
// 
//     private Integer expeditionLevel;
// 
//     @ColumnDefault("3")
//     @Builder.Default
//     private int noChangeThreshold = 3;
// 
//     @ColumnDefault("true")
//     @Builder.Default
//     private boolean isActive = true;
// 
//     @OneToMany(mappedBy = "inspectionCharacter", cascade = CascadeType.ALL, orphanRemoval = true)
//     @JsonManagedReference
//     @OrderBy("recordDate DESC")
//     @Builder.Default
//     private List<CombatPowerHistory> histories = new ArrayList<>();
// 
//     public void updateProfile(String characterImage, double itemLevel, double combatPower,
//                               String serverName, String characterClassName,
//                               String title, String guildName, String townName,
//                               Integer townLevel, Integer expeditionLevel) {
//         this.characterImage = characterImage;
//         this.itemLevel = itemLevel;
//         this.combatPower = Math.max(this.combatPower, combatPower);
//         this.serverName = serverName;
//         this.characterClassName = characterClassName;
//         this.title = title;
//         this.guildName = guildName;
//         this.townName = townName;
//         this.townLevel = townLevel;
//         this.expeditionLevel = expeditionLevel;
//     }
// 
//     public void updateSettings(int noChangeThreshold, boolean isActive) {
//         this.noChangeThreshold = noChangeThreshold;
//         this.isActive = isActive;
//     }
// }
