package lostark.todo.controller.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLoginDto {

    @NotEmpty
    @ApiModelProperty(example = "회원 이메일")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String username;

    @ApiModelProperty(example = "비밀번호")
    private String password;

}
