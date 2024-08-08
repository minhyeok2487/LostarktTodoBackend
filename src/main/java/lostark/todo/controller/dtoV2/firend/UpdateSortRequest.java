package lostark.todo.controller.dtoV2.firend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSortRequest {

    private List<Long> friendIdList;
}
