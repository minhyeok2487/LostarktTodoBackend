package lostark.todo.controller.dto.homeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeFriendsDto {

    private String characterName;

    private double gold;
}
