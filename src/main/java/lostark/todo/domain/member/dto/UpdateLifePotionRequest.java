package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.domain.member.enums.PotionType;

import javax.validation.constraints.NotNull;

@Data
public class UpdateLifePotionRequest {

    @ApiModelProperty(notes = "물약 타입: SMALL, MEDIUM, LARGE")
    @NotNull
    private PotionType type;

    @ApiModelProperty(notes = "더하기 = 양수, 빼기 = 음수")
    @NotNull
    private int num;
}
