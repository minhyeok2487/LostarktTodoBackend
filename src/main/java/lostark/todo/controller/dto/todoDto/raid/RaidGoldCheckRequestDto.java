package lostark.todo.controller.dto.todoDto.raid;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaidGoldCheckRequestDto {

    private long characterId;

    private String characterName;

    @ApiModelProperty(notes = "발탄, 비아 등 컨텐츠 이름")
    private String weekCategory;

    private boolean updateValue;
}
