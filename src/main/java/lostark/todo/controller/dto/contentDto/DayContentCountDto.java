package lostark.todo.controller.dto.contentDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.content.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayContentCountDto {

    @ApiModelProperty(example = "캐릭터 이름")
    String characterName;

    @ApiModelProperty(example = "카테고리(카오스던전 or 가디언토벌")
    Category category;
}
