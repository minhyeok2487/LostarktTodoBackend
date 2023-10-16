package lostark.todo.controller.dto.characterDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDefaultDto {

    private long characterId;

    private String characterName;
}
