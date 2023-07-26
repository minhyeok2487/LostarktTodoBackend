package lostark.todo.controller.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupDto {

    @ApiModelProperty(example = "회원 이름")
    String username;

    @ApiModelProperty(example = "비밀번호")
    String password;

    @ApiModelProperty(example = "로스트아크 api 키")
    String apiKey;

}
