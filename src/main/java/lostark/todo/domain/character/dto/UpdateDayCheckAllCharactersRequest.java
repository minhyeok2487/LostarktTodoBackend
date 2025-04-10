package lostark.todo.domain.character.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateDayCheckAllCharactersRequest  {

    @NotEmpty
    private String serverName;
}
