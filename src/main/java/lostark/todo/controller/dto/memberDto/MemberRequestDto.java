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
public class MemberRequestDto {

    @ApiModelProperty(example = "회원이름")
    private String username;

    @NotEmpty
    @ApiModelProperty(example = "로스트아크 api 키")
    private String apiKey;

    @NotEmpty
    @ApiModelProperty(example = "대표 캐릭터")
    private String characterName;

}
