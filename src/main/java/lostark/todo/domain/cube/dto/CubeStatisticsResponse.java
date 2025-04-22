package lostark.todo.domain.cube.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.entity.CubeContent;
import lostark.todo.domain.market.entity.Market;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CubeStatisticsResponse {

    private String name;

    private double jewelry; //1레벨 보석

    private double leapStone; //돌파석

    private double shilling; //실링

    private double solarGrace; //태양의 은총

    private double solarBlessing; //태양의 축복

    private double solarProtection; //태양의 가호

    private double cardExp;

    private double jewelryPrice;

    private double lavasBreath; //용암의 숨결

    private double glaciersBreath; //빙하의 숨결

    public CubeStatisticsResponse toDto(CubeContent cubeContent, Market market) {
        return CubeStatisticsResponse.builder()
                .name(cubeContent.getName())
                .jewelry(cubeContent.getJewelry())
                .leapStone(cubeContent.getLeapStone())
                .shilling(cubeContent.getShilling())
                .solarGrace(cubeContent.getSolarGrace())
                .solarBlessing(cubeContent.getSolarBlessing())
                .solarProtection(cubeContent.getSolarProtection())
                .lavasBreath(cubeContent.getLavasBreath())
                .glaciersBreath(cubeContent.getGlaciersBreath())
                .cardExp(cubeContent.getCardExp())
                .jewelryPrice(market.getRecentPrice())
                .build();
    }
}
