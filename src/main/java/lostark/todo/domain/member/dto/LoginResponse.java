package lostark.todo.domain.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @NotEmpty
    @ApiModelProperty(example = "회원 id")
    private long id;

    @NotEmpty
    @ApiModelProperty(example = "회원 이름")
    private String username;

    private Role role;

    @ApiModelProperty(example = "jwt")
    private String token;

    public LoginResponse toDto(Member member) {
        return LoginResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .role(member.getRole())
                .build();
    }

}
