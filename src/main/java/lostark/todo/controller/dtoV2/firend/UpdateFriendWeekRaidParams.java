package lostark.todo.controller.dtoV2.firend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFriendWeekRaidParams {

    @NotNull
    private String friendUsername;

    @NotNull
    private long friendCharacterId;

    private List<Long> weekContentIdList;
}
