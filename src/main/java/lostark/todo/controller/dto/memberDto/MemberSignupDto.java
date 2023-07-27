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
public class MemberSignupDto {

    @NotEmpty
    @ApiModelProperty(example = "회원이름")
    String username;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호")
    String password;

    @NotEmpty
    @ApiModelProperty(example = "로스트아크 api 키")
    String apiKey;

    @NotEmpty
    @ApiModelProperty(example = "대표 캐릭터")
    String characterName;

}
