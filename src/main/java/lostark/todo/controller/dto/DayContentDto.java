package lostark.todo.controller.dto;

import lombok.Data;

@Data
public class DayContentDto {

    private Long id;

    private double shilling; //실링

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private double jewelry; //1레벨 보석

    private double gold; //평균 골드
}
