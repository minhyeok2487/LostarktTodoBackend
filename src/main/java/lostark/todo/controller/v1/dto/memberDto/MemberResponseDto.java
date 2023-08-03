package lostark.todo.controller.v1.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.character.Character;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MemberResponseDto {

    @NotEmpty
    @ApiModelProperty(example = "회원 id")
    private long id;

    @NotEmpty
    @ApiModelProperty(example = "회원 이름")
    private String username;

    @NotEmpty
    @ApiModelProperty(example = "캐릭터 리스트")
    private List<String> characters;

    private String token;

}
