package lostark.todo.controller.dto.contentDto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.WeekContent;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeekContentDto {

    private Category category;

    private String name;

    private double level;

    private int gate; //관문

    private double honorShard; //명파

    private double leapStone; //돌파석

    private double destructionStone; //파괴석

    private double guardianStone; //수호석

    private int gold; //골드

    public WeekContent toEntity() {
        return WeekContent.builder()
                .category(category)
                .name(name)
                .level(level)
                .gate(gate)
                .honorShard(honorShard)
                .leapStone(leapStone)
                .destructionStone(destructionStone)
                .guardianStone(guardianStone)
                .gold(gold)
                .build();
    }
}
