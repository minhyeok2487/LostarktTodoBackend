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
public class AuthResponse {

    @NotEmpty
    @ApiModelProperty(example = "회원 id")
    private long memberId;

    @NotEmpty
    @ApiModelProperty(example = "회원 이름")
    private String username;

    @NotEmpty
    @ApiModelProperty(example = "권한")
    private Role role;

    @NotEmpty
    @ApiModelProperty(example = "토큰")
    private String token;

    public AuthResponse toDto(Member member, String token) {
        return AuthResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .role(member.getRole())
                .token(token)
                .build();
    }
}
