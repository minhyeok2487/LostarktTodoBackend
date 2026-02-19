package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateLifePotionRequest {

    @ApiModelProperty(notes = "물약 타입: SMALL, MEDIUM, LARGE")
    @NotNull
    private String type;

    @ApiModelProperty(notes = "더하기 = 양수, 빼기 = 음수")
    @NotNull
    private int num;
}
