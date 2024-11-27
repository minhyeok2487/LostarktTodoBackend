package lostark.todo.domain.character.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateWeekRaidSortRequest extends BaseCharacterRequest {

    private List<SortRequest> sortRequestList;

    @Data
    public static class SortRequest {

        @NotEmpty
        private String weekCategory;

        private int sortNumber;
    }
}

