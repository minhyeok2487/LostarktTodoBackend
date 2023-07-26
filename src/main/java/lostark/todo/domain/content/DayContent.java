package lostark.todo.domain.content;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
public class DayContent extends Content{

    @Enumerated(EnumType.STRING)
    private Category dayContentCategory; // 가디언 토벌, 카오스 던전 분류

    private double shilling; //실링

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private double jewelry; //1레벨 보석

    private double gold; //평균 골드

    protected DayContent() {
    }
}
