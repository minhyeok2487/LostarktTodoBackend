package lostark.todo.controller.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignupDto {

    @NotEmpty
    @ApiModelProperty(example = "회원 이메일")
    private String mail;

    @NotNull
    @ApiModelProperty(example = "이메일 인증번호")
    private int number;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호")
    private String password;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호 확인")
    private String equalPassword;
}
