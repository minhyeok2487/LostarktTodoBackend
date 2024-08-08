package lostark.todo.controller.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCharacterRequest {

    @ApiModelProperty(example = "로스트아크 api 키")
    private String apiKey;

    @ApiModelProperty(example = "대표 캐릭터")
    private String characterName;

}
