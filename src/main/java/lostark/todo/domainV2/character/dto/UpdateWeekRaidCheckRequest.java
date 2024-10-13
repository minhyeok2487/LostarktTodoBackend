package lostark.todo.domainV2.character.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidCheckRequest extends BaseCharacterRequest {

    @NotEmpty
    private List<Long> weekContentIdList;

    private int currentGate;

    private int totalGate;
}
