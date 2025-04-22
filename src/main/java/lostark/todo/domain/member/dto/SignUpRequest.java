package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotEmpty
    @ApiModelProperty(example = "회원 이메일")
    private String mail;

    @NotNull
    @ApiModelProperty(example = "이메일 인증번호")
    private int number;

    @NotEmpty
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "비밀번호는 영문자와 숫자로만 구성되어야 합니다.")
    @ApiModelProperty(example = "비밀번호")
    private String password;

    @NotEmpty
    @ApiModelProperty(example = "비밀번호 확인")
    private String equalPassword;
}
