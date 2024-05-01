package lostark.todo.controller.dtoV2.member;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
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

    @QueryProjection
    public MemberResponse(long memberId, String username, MainCharacterResponse mainCharacter) {
        this.memberId = memberId;
        this.username = username;
        this.mainCharacter = mainCharacter;
    }
}
