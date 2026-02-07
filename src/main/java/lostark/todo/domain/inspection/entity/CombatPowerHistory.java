package lostark.todo.domain.inspection.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_power_history", indexes = {
        @Index(name = "idx_history_char_date", columnList = "inspection_character_id, record_date")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_history_char_date", columnNames = {"inspection_character_id", "record_date"})
})
public class CombatPowerHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "combat_power_history_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_character_id")
    @JsonBackReference
    private InspectionCharacter inspectionCharacter;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private double combatPower;

    private double itemLevel;

    @Column(length = 500)
    private String characterImage;

    @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ArkgridEffectHistory> arkgridEffects = new ArrayList<>();

    @OneToMany(mappedBy = "combatPowerHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @BatchSize(size = 30)
    @Builder.Default
    private List<EquipmentHistory> equipments = new ArrayList<>();

    public void updateData(double combatPower, double itemLevel, String characterImage) {
        this.combatPower = combatPower;
        this.itemLevel = itemLevel;
        this.characterImage = characterImage;
    }

    public void replaceArkgridEffects(List<ArkgridEffectHistory> newEffects) {
        this.arkgridEffects.clear();
        newEffects.forEach(effect -> {
            effect.setCombatPowerHistory(this);
            this.arkgridEffects.add(effect);
        });
    }

    public void replaceEquipments(List<EquipmentHistory> newEquipments) {
        this.equipments.clear();
        newEquipments.forEach(equipment -> {
            equipment.setCombatPowerHistory(this);
            this.equipments.add(equipment);
        });
    }
}
