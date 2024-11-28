package lostark.todo.controller.dtoV2.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.entity.Member;

import javax.validation.constraints.NotEmpty;

import java.util.Optional;

import static lostark.todo.global.Constant.TEST_USERNAME;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
    @NotEmpty
    @ApiModelProperty(example = "회원 id")
    private long memberId;

    @NotEmpty
    @ApiModelProperty(example = "회원 이름")
    private String username;

    @NotEmpty
    @ApiModelProperty(example = "대표 캐릭터")
    private MainCharacterResponse mainCharacter;

    @NotEmpty
    @ApiModelProperty(example = "권한")
    private Role role;

    public static MemberResponse toDto(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .username(createUsername(member))
                .mainCharacter(createMainCharacter(member))
                .role(member.getRole())
                .build();
    }

    private static String createUsername(Member member) {
        return Optional.of(member.getUsername())
                .filter(username -> !username.equals(TEST_USERNAME))
                .orElse(null);
    }

    private static MainCharacterResponse createMainCharacter(Member member) {
        return member.getCharacters().stream()
                .filter(character -> character.getCharacterName().equals(member.getMainCharacterName()))
                .findFirst()
                .map(MainCharacterResponse::new)
                .orElseGet(MainCharacterResponse::new);
    }
}
