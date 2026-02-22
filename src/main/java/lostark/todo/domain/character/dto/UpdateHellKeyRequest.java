package lostark.todo.domain.character.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateHellKeyRequest extends BaseCharacterRequest {

    @ApiModelProperty(notes = "더하기 = 1, 빼기 = -1")
    @NotNull
    private int num;
}
