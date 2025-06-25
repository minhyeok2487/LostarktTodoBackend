package lostark.todo.domain.content.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lostark.todo.domain.content.enums.WeekContentCategory;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class WeekContent extends Content{

    private String weekCategory; //카테고리 : 발탄, 비아키스

    @Enumerated(EnumType.STRING)
    private WeekContentCategory weekContentCategory; //노말 하드

    private int gate; //관문

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private int gold; //골드

    private int characterGold; // 캐릭터 귀속 골드

    private int coolTime; //주기

    private int moreRewardGold; //더보기 골드

    @Override
    public String toString() {
        return "WeekContent{" +
                "weekCategory='" + weekCategory + '\'' +
                ", weekContentCategory=" + weekContentCategory +
                ", gate=" + gate +
                ", honorShard=" + honorShard +
                ", leapStone=" + leapStone +
                ", destructionStone=" + destructionStone +
                ", guardianStone=" + guardianStone +
                ", gold=" + gold +
                ", characterGold=" + characterGold +
                ", coolTime=" + coolTime +
                '}';
    }
}
