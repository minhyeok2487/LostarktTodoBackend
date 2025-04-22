package lostark.todo.domain.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EditScheduleFriendRequest {

    @ApiModelProperty(example = "삭제할 깐부 캐릭터 Id 리스트")
    private List<Long> removeFriendCharacterIdList;

    @ApiModelProperty(example = "추가할 깐부 캐릭터 Id 리스트")
    private List<Long> addFriendCharacterIdList;

}
