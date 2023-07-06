package lostark.todo.domain.content;

import lombok.Data;
import lombok.Getter;
import lostark.todo.controller.dtos.DayContentDto;

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

    //생성 메서드
    public static DayContent createChaos(double shilling, double honorShard, double leapStone,
                                  double destructionStone, double guardianStone,
                                  double jewelry, double gold) {
        DayContent chaosContent = new DayContent();
        chaosContent.dayContentCategory = Category.카오스던전;
        chaosContent.shilling = shilling;
        chaosContent.honorShard = honorShard;
        chaosContent.leapStone = leapStone;
        chaosContent.destructionStone = destructionStone;
        chaosContent.guardianStone = guardianStone;
        chaosContent.jewelry = jewelry;
        chaosContent.gold = gold;
        return chaosContent;
    }

    public DayContent update(DayContentDto dayContentDto) {
        this.shilling = dayContentDto.getShilling();
        this.honorShard = dayContentDto.getHonorShard();
        this.leapStone = dayContentDto.getLeapStone();
        this.destructionStone = dayContentDto.getDestructionStone();
        this.guardianStone = dayContentDto.getGuardianStone();
        this.jewelry = dayContentDto.getJewelry();
        this.gold = dayContentDto.getGold();
        return this;
    }
}
