package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.member.enums.PotionType;

import javax.validation.constraints.NotNull;

@Data
public class UsePotionRequest {

    @ApiModelProperty(notes = "생활의 기운 ID")
    @NotNull
    private Long lifeEnergyId;

    @ApiModelProperty(notes = "물약 타입: LEAP, SMALL, MEDIUM, LARGE")
    @NotNull
    private PotionType type;
}
