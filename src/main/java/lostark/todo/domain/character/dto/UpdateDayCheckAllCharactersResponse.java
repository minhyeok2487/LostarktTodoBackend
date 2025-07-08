package lostark.todo.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateDayCheckAllCharactersResponse {

    private String serverName;

    private boolean done;

    private double profit;
}
