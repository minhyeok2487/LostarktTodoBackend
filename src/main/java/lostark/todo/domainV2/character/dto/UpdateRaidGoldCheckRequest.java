package lostark.todo.domainV2.character.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateRaidGoldCheckRequest extends BaseCharacterRequest {

    @ApiModelProperty(notes = "발탄, 비아 등 컨텐츠 이름")
    private String weekCategory;

    private boolean updateValue;
}
