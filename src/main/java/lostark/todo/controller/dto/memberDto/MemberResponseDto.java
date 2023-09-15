package lostark.todo.controller.dto.memberDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.character.Character;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Character> characters;

    private String token;

}
