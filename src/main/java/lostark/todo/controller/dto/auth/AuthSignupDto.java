package lostark.todo.controller.dto.auth;

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
public class AuthSignupDto {

    @NotEmpty
    @ApiModelProperty(example = "회원 이메일")
    private String email;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호")
    private String password;

    @NotEmpty
    @ApiModelProperty(example = "이메일 인증 완료")
    private boolean isEmail;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호 일치 확인")
    private boolean isPassword;


}
