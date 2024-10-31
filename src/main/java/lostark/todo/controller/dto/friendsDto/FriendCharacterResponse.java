package lostark.todo.controller.dto.friendsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domainV2.friend.entity.FriendSettings;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendCharacterResponse {

    @ApiModelProperty(example = "서버이름 + 캐릭터 리스트")
    private Map<String, List<CharacterDto>> characterDtoMap;

    @ApiModelProperty(example = "깐부 세팅")
    private FriendSettings friendSettings;
}
