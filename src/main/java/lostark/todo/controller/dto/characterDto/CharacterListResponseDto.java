package lostark.todo.controller.dto.characterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterListResponseDto {

    @ApiModelProperty(example = "서버이름 + 캐릭터 리스트")
    private Map<String, List<CharacterDto>> characterDtoMap;

}
