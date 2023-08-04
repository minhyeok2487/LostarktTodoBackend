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
public class MemberLoginDto {

    @NotEmpty
    @ApiModelProperty(example = "회원이름")
    private String username;

    @ApiModelProperty(example = "비밀번호")
    private String password;

}
