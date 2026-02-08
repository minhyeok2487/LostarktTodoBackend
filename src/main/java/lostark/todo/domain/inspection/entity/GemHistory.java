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
@Table(name = "gem_history", indexes = {
        @Index(name = "idx_gem_history", columnList = "combat_power_history_id")
})
public class GemHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gem_history_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combat_power_history_id")
    @JsonBackReference
    private CombatPowerHistory combatPowerHistory;

    @Column(nullable = false)
    private String skillName;

    private int gemSlot;

    @Column(length = 500)
    private String skillIcon;

    @Column(length = 500)
    private String gemIcon;

    private int level;

    private String grade;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String gemOption;
}
