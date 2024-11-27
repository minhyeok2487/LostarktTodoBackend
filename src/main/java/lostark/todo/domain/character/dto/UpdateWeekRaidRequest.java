package lostark.todo.domain.character.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidRequest extends BaseCharacterRequest{

    @NotEmpty
    private List<Long> weekContentIdList;
}
