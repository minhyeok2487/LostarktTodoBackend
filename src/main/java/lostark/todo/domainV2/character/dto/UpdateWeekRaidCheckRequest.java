package lostark.todo.domainV2.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWeekRaidCheckRequest {

    @NotNull
    private long characterId;

    @NotEmpty
    private List<Long> weekContentIdList;

    private int currentGate;

    private int totalGate;
}
