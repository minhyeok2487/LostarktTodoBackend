package lostark.todo.controller.dto.contentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.util.content.entity.CubeContent;
import lostark.todo.domain.util.market.entity.Market;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CubeContentDto {

    private String name;

    private double jewelry; //1레벨 보석

    private double leapStone; //돌파석

    private double shilling; //실링

    private double solarGrace; //태양의 은총

    private double solarBlessing; //태양의 축복

    private double solarProtection; //태양의 가호

    private double cardExp;

    private double jewelryPrice;

    public CubeContentDto toDto(CubeContent cubeContent, Market market) {
        return CubeContentDto.builder()
                .name(cubeContent.getName())
                .jewelry(cubeContent.getJewelry())
                .leapStone(cubeContent.getLeapStone())
                .shilling(cubeContent.getShilling())
                .solarGrace(cubeContent.getSolarGrace())
                .solarBlessing(cubeContent.getSolarBlessing())
                .solarProtection(cubeContent.getSolarProtection())
                .cardExp(cubeContent.getCardExp())
                .jewelryPrice(market.getRecentPrice())
                .build();
    }
}
