package lostark.todo.controller.dtoV2.firend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFriendWeekRaidParams {

    @NotNull
    private String friendUsername;

    @NotNull
    private long friendCharacterId;

    @NotNull
    private long weekContentId;
}
