package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLifePotionsRequest {

    @ApiModelProperty(notes = "생활의 기운 ID")
    @NotNull
    private Long lifeEnergyId;

    @ApiModelProperty(notes = "도약의 물약 수량")
    @NotNull
    @Min(0)
    private int potionLeap;

    @ApiModelProperty(notes = "생명의 물약(소) 수량")
    @NotNull
    @Min(0)
    private int potionSmall;

    @ApiModelProperty(notes = "생명의 물약(중) 수량")
    @NotNull
    @Min(0)
    private int potionMedium;

    @ApiModelProperty(notes = "생명의 물약(대) 수량")
    @NotNull
    @Min(0)
    private int potionLarge;
}
