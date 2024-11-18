package lostark.todo.controller.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCharacterRequest {

    @ApiModelProperty(example = "로스트아크 api 키")
    @NotEmpty
    private String apiKey;

    @ApiModelProperty(example = "대표 캐릭터")
    @NotEmpty
    private String characterName;

}
