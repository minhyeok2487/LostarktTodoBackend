package lostark.todo.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.member.dto.LifeEnergySaveRequest;
import lostark.todo.domain.member.dto.LifeEnergySpendRequest;
import lostark.todo.domain.member.dto.LifeEnergyUpdateRequest;
import lostark.todo.global.entity.BaseTimeEntity;

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
}
