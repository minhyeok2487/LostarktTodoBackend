package lostark.todo.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.member.dto.LifeEnergySaveRequest;
import lostark.todo.domain.member.dto.LifeEnergySpendRequest;
import lostark.todo.domain.member.dto.LifeEnergyUpdateRequest;
import lostark.todo.domain.member.enums.PotionType;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "life_energy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LifeEnergy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "life_energy_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @Column(nullable = false)
    private Integer energy;

    @Column(nullable = false)
    private Integer maxEnergy;

    @Column(nullable = false)
    private String characterName;

    @Column(nullable = false)
    private boolean beatrice; //베아트리스

    @Builder.Default
    private int potionLeap = 0;

    @Builder.Default
    private int potionSmall = 0;

    @Builder.Default
    private int potionMedium = 0;

    @Builder.Default
    private int potionLarge = 0;

    public void update(LifeEnergyUpdateRequest request) {
        this.energy = request.getEnergy();
        this.maxEnergy = request.getMaxEnergy();
        this.characterName = request.getCharacterName();
        this.beatrice = request.isBeatrice();
    }

    public static LifeEnergy toEntity(Member member, LifeEnergySaveRequest request) {
        LifeEnergy lifeEnergy = new LifeEnergy();
        lifeEnergy.member = member;
        lifeEnergy.energy = request.getEnergy();
        lifeEnergy.maxEnergy = request.getMaxEnergy();
        lifeEnergy.characterName = request.getCharacterName();
        lifeEnergy.beatrice = request.isBeatrice();
        return lifeEnergy;
    }

    public void spend(LifeEnergySpendRequest request) {
        this.energy = this.energy - request.getEnergy();
    }

    public void updatePotionCount(PotionType type, int num) {
        switch (type) {
            case LEAP -> this.potionLeap = Math.max(0, this.potionLeap + num);
            case SMALL -> this.potionSmall = Math.max(0, this.potionSmall + num);
            case MEDIUM -> this.potionMedium = Math.max(0, this.potionMedium + num);
            case LARGE -> this.potionLarge = Math.max(0, this.potionLarge + num);
        }
    }

    public int getPotionCount(PotionType type) {
        return switch (type) {
            case LEAP -> this.potionLeap;
            case SMALL -> this.potionSmall;
            case MEDIUM -> this.potionMedium;
            case LARGE -> this.potionLarge;
        };
    }

    public void updatePotions(int potionLeap, int potionSmall, int potionMedium, int potionLarge) {
        this.potionLeap = Math.max(0, potionLeap);
        this.potionSmall = Math.max(0, potionSmall);
        this.potionMedium = Math.max(0, potionMedium);
        this.potionLarge = Math.max(0, potionLarge);
    }

    public void usePotion(PotionType type) {
        if (getPotionCount(type) <= 0) {
            throw new ConditionNotMetException("물약이 부족합니다.");
        }
        updatePotionCount(type, -1);
        this.energy += type.getRecoveryAmount(); // LEAP은 0이므로 energy 변화 없음
    }
}
