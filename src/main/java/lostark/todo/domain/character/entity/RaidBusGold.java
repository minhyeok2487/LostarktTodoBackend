package lostark.todo.domain.character.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.character.dto.UpdateWeekRaidBusGold;
import lostark.todo.global.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RaidBusGold extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raid_bus_gold_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    @JsonBackReference //순환참조 방지
    private Character character;

    private String weekCategory;

    private int busGold;

    @ColumnDefault("true")
    private boolean fixed;

    public void updateWeekRaidBusGold(UpdateWeekRaidBusGold request) {
        this.busGold = request.getBusGold();
        this.fixed = request.isFixed();
    }
}
