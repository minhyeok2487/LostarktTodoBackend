package lostark.todo.controller.v2.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterUpdateListDtoV2 {

    @Valid
    @NotNull(message = "characterDayContentUpdateDtoList가 null 일 수 없습니다.")
    @ApiModelProperty(notes = "캐릭터 이름과 업데이트 항목 리스트")
    private List<CharacterUpdateDtoV2> characterUpdateDtoV2List = new ArrayList<>();

    public void addCharacter(CharacterUpdateDtoV2 result) {
        characterUpdateDtoV2List.add(result);
    }
}
