package lostark.todo.domain.inspection.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ark_passive_history", indexes = {
        @Index(name = "idx_ark_passive_history", columnList = "combat_power_history_id")
})
public class ArkPassiveHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ark_passive_history_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combat_power_history_id")
    @JsonBackReference
    private CombatPowerHistory combatPowerHistory;

    @Column(nullable = false)
    private String category;

    private String name;

    private int level;

    @Column(length = 500)
    private String icon;

    @Column(columnDefinition = "TEXT")
    private String description;
}
