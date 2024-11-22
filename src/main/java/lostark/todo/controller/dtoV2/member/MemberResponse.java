package lostark.todo.controller.dtoV2.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.Role;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.member.entity.Member;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
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

    public MemberResponse(Member member) {
        this.memberId = member.getId();
        this.username = member.getUsername();
        this.mainCharacter = createMainCharacter(member);
        this.role = member.getRole();
    }

    private MainCharacterResponse createMainCharacter(Member member) {
        for (Character character : member.getCharacters()) {
            if (character.getCharacterName().equals(member.getMainCharacterName())) {
                return new MainCharacterResponse(character);
            }
        }
        return new MainCharacterResponse();
    }
}
