package lostark.todo.controller.dto.homeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRaidDto {

    private String name;

    private int dealerCount;

    private int supportCount;

    private int count;

    private int totalCount;
}
