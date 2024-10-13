package lostark.todo.domainV2.character.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWeekRaidSortRequest {

    @NotNull
    private long characterId;

    private List<SortRequest> sortRequestList;

    @Data
    public static class SortRequest {

        @NotEmpty
        private String weekCategory;

        private int sortNumber;
    }
}

